package com.calaratjada.insider.data.service

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.calaratjada.insider.data.local.ChatDao
import com.calaratjada.insider.data.model.CoinTransaction
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

data class CoinPackage(
    val productId: String,
    val coins: Int,
    val priceLabel: String,
    val description: String,
    val productDetails: ProductDetails? = null
)

@Singleton
class CoinManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val chatDao: ChatDao,
    private val adManager: AdManager
) : PurchasesUpdatedListener {

    companion object {
        private const val TAG = "CoinManager"

        // Coin costs
        const val COST_SEND_MESSAGE = 1
        const val COST_SEND_IMAGE = 2
        const val COST_UNLOCK_ROOM_24H = 5
        const val COST_MAP_EVENT_24H = 20
        const val COST_MAP_EVENT_7D = 75
        const val COST_PREMIUM_30D = 150
        const val COST_CUSTOM_ROOM = 40
        const val COST_PROFILE_BOOST = 30

        const val FREE_ROOM_ID = "general"
        const val DAILY_BONUS = 1
        const val SIGNUP_BONUS = 10

        // Ad-free product
        const val PRODUCT_AD_FREE = "ad_free_forever"
        const val AD_FREE_PRICE_LABEL = "4,99 €"

        val COIN_PACKAGES = listOf(
            CoinPackage("coins_100", 100, "0,99 €", "100 Coins"),
            CoinPackage("coins_500", 500, "3,99 €", "500 Coins – 20% Rabatt"),
            CoinPackage("coins_1200", 1200, "7,99 €", "1.200 Coins – 33% Rabatt"),
            CoinPackage("coins_3000", 3000, "16,99 €", "3.000 Coins – 43% Rabatt"),
            CoinPackage("coins_7500", 7500, "34,99 €", "7.500 Coins – 53% Rabatt")
        )
    }

    private var billingClient: BillingClient? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _packages = MutableStateFlow(COIN_PACKAGES)
    val packages = _packages.asStateFlow()

    private val _coinBalance = MutableStateFlow(0)
    val coinBalance = _coinBalance.asStateFlow()

    // Ad-free purchase
    private var adFreeProductDetails: ProductDetails? = null
    private val _adFreePriceLabel = MutableStateFlow(AD_FREE_PRICE_LABEL)
    val adFreePriceLabel = _adFreePriceLabel.asStateFlow()

    fun initialize() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProducts()
                    restoreAdFreePurchase()
                }
            }
            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected")
            }
        })
    }

    private fun queryProducts() {
        scope.launch {
            // Query coin packages (consumable)
            val productList = COIN_PACKAGES.map { pkg ->
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(pkg.productId)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            }
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            val productResult = billingClient?.queryProductDetails(params)
            if (productResult?.billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                val detailsList = productResult.productDetailsList ?: emptyList()
                val updatedPackages = COIN_PACKAGES.map { pkg ->
                    val details = detailsList.find { it.productId == pkg.productId }
                    if (details != null) {
                        val price = details.oneTimePurchaseOfferDetails?.formattedPrice ?: pkg.priceLabel
                        pkg.copy(priceLabel = price, productDetails = details)
                    } else pkg
                }
                _packages.value = updatedPackages
            }

            // Query ad-free product (non-consumable)
            val adFreeList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PRODUCT_AD_FREE)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )
            val adFreeResult = billingClient?.queryProductDetails(
                QueryProductDetailsParams.newBuilder().setProductList(adFreeList).build()
            )
            if (adFreeResult?.billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                adFreeResult.productDetailsList?.firstOrNull()?.let { details ->
                    adFreeProductDetails = details
                    details.oneTimePurchaseOfferDetails?.formattedPrice?.let {
                        _adFreePriceLabel.value = it
                    }
                }
            }
        }
    }

    /**
     * Restore ad-free purchase on app restart
     */
    private fun restoreAdFreePurchase() {
        scope.launch {
            val purchasesResult = billingClient?.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )
            if (purchasesResult?.billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                val adFreePurchase = purchasesResult.purchasesList.find {
                    it.products.contains(PRODUCT_AD_FREE) &&
                    it.purchaseState == Purchase.PurchaseState.PURCHASED
                }
                if (adFreePurchase != null) {
                    adManager.setAdFree()
                }
            }
        }
    }

    fun launchAdFreePurchase(activity: Activity) {
        val productDetails = adFreeProductDetails ?: return
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            )
            .build()
        billingClient?.launchBillingFlow(activity, flowParams)
    }

    fun launchPurchase(activity: Activity, coinPackage: CoinPackage) {
        val productDetails = coinPackage.productDetails ?: return
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            )
            .build()
        billingClient?.launchBillingFlow(activity, flowParams)
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return

        // Check if this is the ad-free purchase (non-consumable → acknowledge)
        if (purchase.products.contains(PRODUCT_AD_FREE)) {
            if (!purchase.isAcknowledged) {
                val ackParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient?.acknowledgePurchase(ackParams) { result ->
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        adManager.setAdFree()
                        Log.d(TAG, "Ad-free purchase acknowledged")
                    }
                }
            } else {
                adManager.setAdFree()
            }
            return
        }

        // Coin package purchase (consumable)
        val pkg = COIN_PACKAGES.find { purchase.products.contains(it.productId) } ?: return

        // Consume the purchase so it can be bought again
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient?.consumeAsync(consumeParams) { consumeResult, _ ->
            if (consumeResult.responseCode == BillingClient.BillingResponseCode.OK) {
                scope.launch {
                    creditCoins(pkg.coins, "Kauf: ${pkg.description}")
                }
            }
        }
    }

    suspend fun creditCoins(amount: Int, description: String) {
        chatDao.addCoins(amount)
        chatDao.insertTransaction(
            CoinTransaction(
                type = "purchase",
                amount = amount,
                description = description
            )
        )
        refreshBalance()
    }

    suspend fun spendCoins(amount: Int, type: String, description: String): Boolean {
        val balance = chatDao.getCoinBalance() ?: 0
        if (balance < amount) return false
        val rows = chatDao.spendCoins(amount)
        if (rows > 0) {
            chatDao.insertTransaction(
                CoinTransaction(
                    type = type,
                    amount = -amount,
                    description = description
                )
            )
            refreshBalance()
            return true
        }
        return false
    }

    suspend fun canAfford(amount: Int): Boolean {
        val balance = chatDao.getCoinBalance() ?: 0
        return balance >= amount
    }

    suspend fun refreshBalance() {
        _coinBalance.value = chatDao.getCoinBalance() ?: 0
    }

    fun getTransactions() = chatDao.getAllTransactions()

    fun destroy() {
        billingClient?.endConnection()
    }
}
