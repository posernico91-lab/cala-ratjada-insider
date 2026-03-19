package com.calaratjada.insider.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calaratjada.insider.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Datenschutzerklärung", fontWeight = FontWeight.Bold) },
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
            SectionTitle("1. Verantwortlicher")
            BodyText("""
                Nico Poser
                Beethovenstraße 84
                42655 Solingen
                E-Mail: posernico91@gmail.com
                Telefon: 015678414331
            """.trimIndent())

            SectionTitle("2. Erhobene Daten")
            BodyText("""
                Wir erheben und verarbeiten folgende personenbezogene Daten:
                
                • Google-Konto: E-Mail-Adresse, Anzeigenname, Profilbild (bei Google-Anmeldung)
                • Standortdaten: Breitengrad/Längengrad (nur bei aktiver Nutzung der Kartenfunktion und mit Ihrer Einwilligung)
                • Chat-Nachrichten: Textnachrichten und Bilder im Community-Chat
                • Nutzungsdaten: Coin-Transaktionen, Kaufhistorie
                • Geräte-Informationen: IP-Adresse (bei API-Aufrufen)
                • Werbe-ID (Advertising ID / GAID): Zur Anzeige personalisierter Werbung (nur mit Ihre Einwilligung)
                • Werbenetzwerk-Daten: Von Appodeal und verbundenen Ad-Mediation-Partnern erhobene Nutzungsdaten
            """.trimIndent())

            SectionTitle("3. Zweck der Datenverarbeitung")
            BodyText("""
                • Bereitstellung der App-Funktionen (Chat, Karte, Events, Wetter)
                • Authentifizierung und Kontoverwaltung
                • In-App-Käufe und Coin-System
                • Werbeanzeigen (nur mit Ihrer Einwilligung)
                • Verbesserung unserer Dienste
            """.trimIndent())

            SectionTitle("4. Rechtsgrundlage (DSGVO Art. 6)")
            BodyText("""
                • Art. 6 Abs. 1 lit. a DSGVO: Einwilligung (Werbung, Standort)
                • Art. 6 Abs. 1 lit. b DSGVO: Vertragserfüllung (App-Nutzung, Käufe)
                • Art. 6 Abs. 1 lit. f DSGVO: Berechtigte Interessen (Sicherheit, Missbrauchsschutz)
            """.trimIndent())

            SectionTitle("5. Drittanbieter & Datenübermittlung")
            BodyText("""
                Wir nutzen folgende Dienste (Datenverarbeitung teilweise außerhalb der EU):
                
                • Google LLC (USA): Authentifizierung, Maps, AdMob-Werbung, Google CMP (TCF v2)
                  → Standardvertragsklauseln gemäß Art. 46 Abs. 2 lit. c DSGVO
                • Appodeal Inc. (USA): Ad-Mediation-Plattform und Drittanbieter-Werbenetzwerke
                  → Datenschutzrichtlinie: https://appodeal.com/privacy-policy
                  → Standardvertragsklauseln
                • Unity Technologies (USA): Werbeanzeigen (Unity Ads)
                  → Standardvertragsklauseln
                • Vivox/Unity (USA): Voice-/Text-Chat
                  → Standardvertragsklauseln
                • OpenWeatherMap (UK): Wetterdaten (keine personenbezogenen Daten)
                • Travelpayouts (Russland): Reise-Affiliate-Links
                  → Nur bei aktiver Nutzung der Buchungsfunktion
                
                Diese Drittanbieter können folgende Daten erheben:
                • IP-Adresse
                • Werbe-ID (Advertising ID / GAID)
                • Geräte-Informationen (Modell, Betriebssystem, Bildschirmauflösung)
                • Nutzungsverhalten innerhalb der App (für Werbezwecke)
            """.trimIndent())

            SectionTitle("6. Datenspeicherung & Aufbewahrung")
            BodyText("""
                • Chat-Nachrichten: Automatische Löschung nach 90 Tagen
                • Standortdaten: Nur in Echtzeit, keine dauerhafte Speicherung
                • Profildaten: Bis zur Kontolöschung
                • Kaufdaten: Gemäß gesetzlicher Aufbewahrungsfristen
                • Alle lokalen Daten werden verschlüsselt gespeichert (SQLCipher, AES-256)
            """.trimIndent())

            SectionTitle("7. Ihre Rechte (DSGVO Art. 15-22)")
            BodyText("""
                Sie haben folgende Rechte:
                
                • Auskunftsrecht (Art. 15): Welche Daten wir über Sie speichern
                • Berichtigungsrecht (Art. 16): Korrektur unrichtiger Daten
                • Löschungsrecht (Art. 17): Löschung Ihrer Daten ("Recht auf Vergessenwerden")
                • Einschränkung der Verarbeitung (Art. 18)
                • Datenübertragbarkeit (Art. 20): Export Ihrer Daten
                • Widerspruchsrecht (Art. 21)
                • Recht auf Widerruf der Einwilligung (Art. 7 Abs. 3)
                
                Zur Ausübung Ihrer Rechte kontaktieren Sie uns unter:
                posernico91@gmail.com
            """.trimIndent())

            SectionTitle("8. Widerruf der Einwilligung")
            BodyText("""
                Sie können Ihre Einwilligung jederzeit widerrufen:
                
                • Werbe-Tracking: In den App-Einstellungen oder über Ihr Profil
                • Standortfreigabe: In den Geräteeinstellungen
                • Kontolöschung: Über Profil → "Alle Daten löschen"
                
                Der Widerruf berührt nicht die Rechtmäßigkeit der aufgrund der
                Einwilligung bis zum Widerruf erfolgten Verarbeitung.
            """.trimIndent())

            SectionTitle("9. Datensicherheit")
            BodyText("""
                Wir schützen Ihre Daten durch:
                
                • Verschlüsselte Datenbank (AES-256/SQLCipher)
                • Verschlüsselte Einstellungen (EncryptedSharedPreferences)
                • HTTPS-Kommunikation für alle API-Aufrufe
                • Android Keystore für kryptographische Schlüssel
                • Keine Speicherung von Passwörtern
            """.trimIndent())

            SectionTitle("10. Beschwerderecht")
            BodyText("""
                Sie haben das Recht, sich bei einer Datenschutz-Aufsichtsbehörde zu beschweren:
                
                Landesbeauftragte für Datenschutz und Informationsfreiheit
                Nordrhein-Westfalen
                Kavalleriestraße 2-4
                40213 Düsseldorf
                https://www.ldi.nrw.de
            """.trimIndent())

            SectionTitle("11. Google CMP & IAB TCF v2")
            BodyText("""
                Für die Einholung Ihrer Einwilligung zur Datenverarbeitung im Rahmen der Werbung verwenden wir die Google Consent Management Platform (CMP), die konform mit dem IAB Transparency and Consent Framework (TCF) v2 ist.
                
                Bei erstmaliger Nutzung der App wird Ihnen ein Einwilligungsformular angezeigt, in dem Sie Ihre Präferenzen zu personalisierter Werbung und Datenverarbeitung festlegen können. Diese Einstellungen werden an alle integrierten Werbenetzwerke (einschließlich Appodeal und dessen Mediation-Partner) weitergegeben.
                
                Sie können Ihre Einwilligung jederzeit widerrufen, indem Sie in den Profil-Einstellungen Ihre Datenschutzpräferenzen ändern.
            """.trimIndent())

            SectionTitle("12. Änderungen")
            BodyText("Diese Datenschutzerklärung kann aktualisiert werden. Die aktuelle Version ist stets in der App verfügbar.")

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Stand: März 2026",
                fontSize = 12.sp,
                color = Stone400
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Stone900
    )
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun BodyText(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = Stone700,
        lineHeight = 20.sp
    )
}
