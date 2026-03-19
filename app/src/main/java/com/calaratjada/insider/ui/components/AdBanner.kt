package com.calaratjada.insider.ui.components

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.calaratjada.insider.BuildConfig
import com.calaratjada.insider.ui.theme.Stone100
import com.calaratjada.insider.ui.theme.Stone400
import com.calaratjada.insider.ui.viewmodel.AdViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdBanner(
    modifier: Modifier = Modifier,
    adViewModel: AdViewModel = hiltViewModel()
) {
    val isAdFree by adViewModel.isAdFree.collectAsStateWithLifecycle()
    if (isAdFree) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Stone100)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ANZEIGE",
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = Stone400,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        AndroidView(
            modifier = Modifier
                .width(320.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(8.dp)),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = BuildConfig.ADMOB_BANNER_ID
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
