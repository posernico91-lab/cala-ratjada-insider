package com.calaratjada.insider.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calaratjada.insider.ui.theme.*

@Composable
fun LoginScreen(
    onGoogleSignIn: (Intent) -> Unit,
    getSignInIntent: () -> Intent,
    onSkip: () -> Unit
) {
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { onGoogleSignIn(it) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Emerald700, Emerald500, Stone50)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Stone50),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.BeachAccess,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Emerald500
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Cala Ratjada Insider",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Stone900,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Melde dich an, um den Community\nChat zu nutzen und Coins zu verdienen",
                    fontSize = 14.sp,
                    color = Stone500,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Free coins banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Amber400.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = Amber400,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "10 Gratis-Coins bei Anmeldung!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Stone800
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Google Sign-In Button
                OutlinedButton(
                    onClick = { signInLauncher.launch(getSignInIntent()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mit Google anmelden",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Stone800
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onSkip) {
                    Text(
                        text = "Später anmelden",
                        color = Stone400,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
