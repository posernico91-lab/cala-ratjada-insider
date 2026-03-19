package com.calaratjada.insider.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calaratjada.insider.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpressumScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Impressum", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Zurück")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Impressum",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Stone900
            )
            Text(
                text = "Angaben gemäß § 5 TMG",
                fontSize = 13.sp,
                color = Stone500
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Name
            Text(
                text = "Nico Poser",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Stone800
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Address
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.LocationOn, null, tint = Emerald500, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Beethovenstraße 84", fontSize = 15.sp, color = Stone700)
                    Text("42655 Solingen", fontSize = 15.sp, color = Stone700)
                    Text("Deutschland", fontSize = 15.sp, color = Stone700)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Phone
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, null, tint = Emerald500, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("015678414331", fontSize = 15.sp, color = Stone700)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Email
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, null, tint = Emerald500, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("posernico91@gmail.com", fontSize = 15.sp, color = Stone700)
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = Stone200)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Haftungsausschluss",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Stone900
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = """
                    Haftung für Inhalte:
                    Die Inhalte unserer App wurden mit größter Sorgfalt erstellt. Für die Richtigkeit, Vollständigkeit und Aktualität der Inhalte können wir jedoch keine Gewähr übernehmen. Als Diensteanbieter sind wir gemäß § 7 Abs. 1 TMG für eigene Inhalte in dieser App nach den allgemeinen Gesetzen verantwortlich.
                    
                    Haftung für Links:
                    Unsere App enthält Links zu externen Websites Dritter (z.B. Travelpayouts Buchungslinks), auf deren Inhalte wir keinen Einfluss haben. Für die Inhalte der verlinkten Seiten ist stets der jeweilige Anbieter oder Betreiber verantwortlich.
                    
                    Urheberrecht:
                    Die durch den App-Betreiber erstellten Inhalte und Werke unterliegen dem deutschen Urheberrecht.
                """.trimIndent(),
                fontSize = 14.sp,
                color = Stone700,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Stone200)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Streitschlichtung",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Stone900
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = """
                    Die Europäische Kommission stellt eine Plattform zur Online-Streitbeilegung (OS) bereit: https://ec.europa.eu/consumers/odr/
                    
                    Wir sind nicht bereit oder verpflichtet, an Streitbeilegungsverfahren vor einer Verbraucherschlichtungsstelle teilzunehmen.
                """.trimIndent(),
                fontSize = 14.sp,
                color = Stone700,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
