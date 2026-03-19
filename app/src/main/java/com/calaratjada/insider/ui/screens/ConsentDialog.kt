package com.calaratjada.insider.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.calaratjada.insider.ui.theme.*

/**
 * DSGVO/GDPR first-launch consent dialog.
 * Must be shown before any data collection, ad loading, or analytics.
 */
@Composable
fun ConsentDialog(
    onConsent: (privacyAccepted: Boolean, adsConsent: Boolean, locationConsent: Boolean) -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onImpressumClick: () -> Unit
) {
    var privacyAccepted by remember { mutableStateOf(false) }
    var adsConsent by remember { mutableStateOf(false) }
    var locationConsent by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { /* nicht schließbar ohne Aktion */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Stone50),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Willkommen bei Cala Ratjada Insider!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Stone900
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Bevor du loslegst, benötigen wir deine Einwilligung gemäß DSGVO (Art. 6, 7).",
                    fontSize = 14.sp,
                    color = Stone600,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Privacy Policy Consent (mandatory)
                ConsentCheckItem(
                    checked = privacyAccepted,
                    onCheckedChange = { privacyAccepted = it },
                    title = "Datenschutzerklärung akzeptieren *",
                    description = "Ich habe die Datenschutzerklärung gelesen und stimme der Verarbeitung meiner Daten zu.",
                    required = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Ads Consent (optional)
                ConsentCheckItem(
                    checked = adsConsent,
                    onCheckedChange = { adsConsent = it },
                    title = "Personalisierte Werbung",
                    description = "Ich stimme der Anzeige personalisierter Werbung durch Google AdMob und Unity Ads zu.",
                    required = false
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Location Consent (optional)
                ConsentCheckItem(
                    checked = locationConsent,
                    onCheckedChange = { locationConsent = it },
                    title = "Standortdaten teilen",
                    description = "Ich stimme der Nutzung meines Standorts für Kartenfeatures und Community-Events zu.",
                    required = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Links
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onPrivacyPolicyClick) {
                        Text("Datenschutz", color = Emerald600, fontSize = 13.sp)
                    }
                    TextButton(onClick = onImpressumClick) {
                        Text("Impressum", color = Emerald600, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Accept Button
                Button(
                    onClick = { onConsent(privacyAccepted, adsConsent, locationConsent) },
                    enabled = privacyAccepted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Emerald600,
                        disabledContainerColor = Stone300
                    )
                ) {
                    Text(
                        text = if (privacyAccepted) "Zustimmen & Weiter" else "Bitte Datenschutz akzeptieren",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "* Pflichtfeld. Du kannst deine Einwilligung jederzeit in den Profileinstellungen widerrufen.",
                    fontSize = 11.sp,
                    color = Stone400,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ConsentCheckItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    description: String,
    required: Boolean
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (checked) Emerald50 else Stone100
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Emerald600,
                    uncheckedColor = Stone400
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = if (required) Stone900 else Stone700
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Stone500,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
