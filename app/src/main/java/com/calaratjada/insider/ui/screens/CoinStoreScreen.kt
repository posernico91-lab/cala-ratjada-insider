package com.calaratjada.insider.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.calaratjada.insider.data.service.AdManager
import com.calaratjada.insider.data.service.CoinManager
import com.calaratjada.insider.data.service.CoinPackage
import com.calaratjada.insider.ui.theme.*
import com.calaratjada.insider.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinStoreScreen(
    onBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val coinBalance by viewModel.coinBalance.collectAsStateWithLifecycle()
    val packages by viewModel.coinManager.packages.collectAsStateWithLifecycle()
    val isAdFree by viewModel.adManager.isAdFree.collectAsStateWithLifecycle()
    val adFreePriceLabel by viewModel.coinManager.adFreePriceLabel.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coin Shop", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50)
            )
        },
        containerColor = Stone50
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Current balance
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Emerald600),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$coinBalance",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Dein Guthaben",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // ===== AD-FREE PURCHASE =====
            item {
                if (!isAdFree) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Stone900),
                        elevation = CardDefaults.cardElevation(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Block,
                                    contentDescription = null,
                                    tint = Amber400,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Werbefrei für immer!",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Einmaliger Kauf – Keine Banner, keine Interstitials, keine App-Open-Ads. Für immer.",
                                fontSize = 13.sp,
                                color = Stone400,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    (context as? Activity)?.let { activity ->
                                        viewModel.coinManager.launchAdFreePurchase(activity)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.VerifiedUser, null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Werbefrei kaufen – $adFreePriceLabel",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Emerald50),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = Emerald500, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Du genießt werbefreie Nutzung!",
                                fontWeight = FontWeight.SemiBold,
                                color = Emerald700
                            )
                        }
                    }
                }
            }

            // ===== REWARDED VIDEO =====
            if (!isAdFree) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Amber400.copy(alpha = 0.15f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = Amber500,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Gratis Coins!",
                                    fontWeight = FontWeight.Bold,
                                    color = Stone900
                                )
                                Text(
                                    text = "Sieh dir ein kurzes Video an und erhalte ${AdManager.REWARDED_COINS} Coins",
                                    fontSize = 12.sp,
                                    color = Stone600
                                )
                            }
                            Button(
                                onClick = {
                                    (context as? Activity)?.let { activity ->
                                        viewModel.watchRewardedAd(activity)
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Amber500)
                            ) {
                                Text("Ansehen", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Pricing info
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Stone100)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Was kosten Coins?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Stone900
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CoinCostRow("Nachricht senden", "${CoinManager.COST_SEND_MESSAGE} Coin")
                        CoinCostRow("Bild senden", "${CoinManager.COST_SEND_IMAGE} Coins")
                        CoinCostRow("Raum entsperren (24h)", "${CoinManager.COST_UNLOCK_ROOM_24H} Coins")
                        CoinCostRow("Map-Event (24h)", "${CoinManager.COST_MAP_EVENT_24H} Coins")
                        CoinCostRow("Map-Event (7 Tage)", "${CoinManager.COST_MAP_EVENT_7D} Coins")
                        CoinCostRow("Eigener Chatraum", "${CoinManager.COST_CUSTOM_ROOM} Coins")
                        CoinCostRow("Profil-Boost (24h)", "${CoinManager.COST_PROFILE_BOOST} Coins")
                        CoinCostRow("Premium (30 Tage)", "${CoinManager.COST_PREMIUM_30D} Coins")
                    }
                }
            }

            // Coin packages header
            item {
                Text(
                    text = "Coins kaufen",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Stone900,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Coin packages
            items(packages) { pkg ->
                CoinPackageCard(
                    coinPackage = pkg,
                    onBuy = {
                        (context as? Activity)?.let { activity ->
                            viewModel.coinManager.launchPurchase(activity, pkg)
                        }
                    }
                )
            }

            // Daily bonus info
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Amber400.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CardGiftcard, null, tint = Amber400)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Täglicher Bonus",
                                fontWeight = FontWeight.SemiBold,
                                color = Stone800
                            )
                            Text(
                                "Öffne die App täglich für ${CoinManager.DAILY_BONUS} Gratis-Coin!",
                                fontSize = 13.sp,
                                color = Stone500
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CoinCostRow(label: String, cost: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = Stone600)
        Text(text = cost, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Emerald700)
    }
}

@Composable
private fun CoinPackageCard(
    coinPackage: CoinPackage,
    onBuy: () -> Unit
) {
    val isBestValue = coinPackage.coins >= 3000

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isBestValue) Emerald50 else Stone100
        ),
        elevation = CardDefaults.cardElevation(if (isBestValue) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coin icon + amount
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = Amber400,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${coinPackage.coins}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Stone900
                    )
                }
                Text(
                    text = coinPackage.description,
                    fontSize = 12.sp,
                    color = Stone500
                )
                if (isBestValue) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Emerald500,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = " BELIEBT ",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Button(
                onClick = onBuy,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isBestValue) Emerald600 else Stone700
                )
            ) {
                Text(
                    text = coinPackage.priceLabel,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
