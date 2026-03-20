package com.calaratjada.insider.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.calaratjada.insider.BuildConfig
import com.calaratjada.insider.config.ActiveCountryConfig
import com.calaratjada.insider.ui.theme.*

private enum class BookingTab(
    val label: String,
    val icon: ImageVector,
    val widgetUrlTemplate: String
) {
    FLIGHTS(
        "Flüge",
        Icons.Default.Flight,
        "https://c137.travelpayouts.com/content?promo_id=4497&shmarker=&locale={locale}&currency={currency}"
    ),
    CARS(
        "Mietwagen",
        Icons.Default.DirectionsCar,
        "https://c87.travelpayouts.com/content?promo_id=2466&shmarker=&locale={locale}&currency={currency}"
    ),
    TOURS(
        "Touren",
        Icons.Default.ConfirmationNumber,
        "https://c112.travelpayouts.com/content?promo_id=4487&shmarker=&locale={locale}&currency={currency}"
    );

    fun getWidgetHtml(): String {
        val locale = ActiveCountryConfig.defaultLocale.take(2)
        val currency = ActiveCountryConfig.currencyCode
        val url = widgetUrlTemplate
            .replace("{locale}", locale)
            .replace("{currency}", currency)
        return """<div id="tp-widget"></div><script src="$url"></script>"""
    }
}

@Composable
fun BookingScreen() {
    var selectedTab by remember { mutableStateOf(BookingTab.FLIGHTS) }
    var showWebView by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Stone900
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Flight,
                    contentDescription = null,
                    tint = Emerald400,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Reise planen",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Flüge, Hotels, Mietwagen & Touren",
                    color = Stone400,
                    fontSize = 13.sp
                )
            }
        }

        // Tab row
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color.White,
            contentColor = Emerald500
        ) {
            BookingTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = {
                        selectedTab = tab
                        showWebView = false
                    },
                    text = {
                        Text(
                            text = tab.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }
        }

        if (showWebView) {
            // WebView with Travelpayouts widget
            BookingWebView(
                tab = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        } else {
            // Landing content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = selectedTab.icon,
                    contentDescription = null,
                    tint = Emerald500.copy(alpha = 0.3f),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "${selectedTab.label} finden",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Stone900
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Vergleiche Preise und buche direkt die besten Angebote für ${ActiveCountryConfig.primaryCity}.",
                    fontSize = 14.sp,
                    color = Stone500,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { showWebView = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${selectedTab.label} suchen",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        // Direct affiliate link
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Direkt buchen",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun BookingWebView(tab: BookingTab, modifier: Modifier = Modifier) {
    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <meta http-equiv="Content-Security-Policy" content="default-src https:; script-src https://c137.travelpayouts.com https://c87.travelpayouts.com https://c112.travelpayouts.com; style-src 'unsafe-inline'; frame-src https:;">
            <style>
                body { margin: 0; padding: 16px; font-family: sans-serif; background: #FAFAF9; }
            </style>
        </head>
        <body>
            ${tab.getWidgetHtml()}
        </body>
        </html>
    """.trimIndent()

    // Allowed domains for Travelpayouts widgets
    val allowedDomains = listOf(
        "travelpayouts.com", "trip.tpx.gr", "aviasales", "jetradar",
        "hotellook.com", "travelportawards"
    )

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true // Required for Travelpayouts widgets
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                // Security hardening
                settings.allowFileAccess = false
                settings.allowContentAccess = false
                settings.setGeolocationEnabled(false)
                settings.databaseEnabled = false
                @Suppress("DEPRECATION")
                settings.saveFormData = false

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url?.toString() ?: return true
                        // Only allow HTTPS to trusted domains
                        if (!url.startsWith("https://")) return true
                        return !allowedDomains.any { url.contains(it) }
                    }
                }
                loadDataWithBaseURL(
                    "https://www.travelpayouts.com",
                    htmlContent,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(
                "https://www.travelpayouts.com",
                htmlContent,
                "text/html",
                "UTF-8",
                null
            )
        }
    )
}
