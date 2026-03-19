# 🏝️ Island Insider – Android Travel App Template

## Übersicht
Dieses Template ist die Grundlage für alle "Insider"-Reise-Apps.
Es basiert auf der **Cala Ratjada Insider** App und kann für jede beliebige Destination angepasst werden.

## Tech-Stack
- **Kotlin 2.0.20** + **Jetpack Compose** (Material3)
- **MVVM** + **Hilt** (Dependency Injection)
- **Room** (SQLCipher-verschlüsselt) + **Retrofit** + **Coil**
- **Google Maps Compose** + **Google Sign-In**
- **Vivox SDK** (Voice/Text Chat, C++ JNI)
- **Google Play Billing** (In-App-Käufe, Coin-System)
- **AdMob** + **Unity Ads** + **Appodeal** (Mediation mit 70+ Netzwerken)
- **Google UMP** (TCF v2 DSGVO-Consent)
- **EncryptedSharedPreferences** + **Android Keystore**
- **WorkManager** (Hilt-Worker, 90-Tage Message-Cleanup)

## Projektstruktur
```
app/src/main/java/com/{PACKAGE_PATH}/
├── {AppName}App.kt              # Application (Hilt, WorkManager, AdManager, ConsentManager)
├── MainActivity.kt              # Entry Point (UMP Consent, Appodeal Init, App Open Ad)
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt       # Room DB (SQLCipher)
│   │   ├── ChatDao.kt           # Chat/Friends/MapEvents DAOs
│   │   ├── EventDao.kt          # Events DAO
│   │   └── PoiDao.kt            # Points of Interest DAO
│   ├── model/
│   │   ├── Category.kt          # POI-Kategorien enum
│   │   ├── ChatMessage.kt       # Chat-Nachricht Entity
│   │   ├── ChatRoom.kt          # Chat-Raum Entity
│   │   ├── ChatUser.kt          # Chat-Benutzer Entity
│   │   ├── CoinTransaction.kt   # Coin-Transaktion Entity
│   │   ├── Event.kt             # Event Entity
│   │   ├── Friend.kt            # Freund Entity
│   │   ├── MapEvent.kt          # Map-Event Entity
│   │   ├── Poi.kt               # Point of Interest Entity
│   │   ├── UserProfile.kt       # Benutzerprofil Entity
│   │   └── Weather.kt           # Wetter-Modell
│   ├── remote/
│   │   ├── WeatherApi.kt        # Retrofit Weather API Interface
│   │   └── WeatherResponse.kt   # API Response Model
│   ├── repository/
│   │   ├── ChatRepository.kt    # Chat + Message + Friend Repository
│   │   ├── EventRepository.kt   # Events Repository
│   │   ├── PoiRepository.kt     # POI Repository
│   │   └── WeatherRepository.kt # Weather Repository
│   ├── service/
│   │   ├── AdManager.kt         # AdMob + Unity + Appodeal (Fallback-Kette)
│   │   ├── AuthService.kt       # Google Sign-In
│   │   ├── CoinManager.kt       # Coin-System + In-App-Purchases
│   │   ├── ConsentManager.kt    # DSGVO/GDPR + UMP + EncryptedPrefs
│   │   ├── VivoxNative.kt       # JNI Bridge zu Vivox C++
│   │   ├── VivoxService.kt      # Vivox Voice/Text Service
│   │   └── VivoxTokenGenerator.kt # Vivox Auth Tokens
│   └── SeedData.kt              # Initiale POI/Event-Daten
├── di/
│   └── AppModule.kt             # Hilt Module (DB, API, Repos)
├── ui/
│   ├── components/
│   │   ├── AdBanner.kt          # AdMob Banner Composable
│   │   ├── CategoryChip.kt      # Kategorie-Filter Chip
│   │   ├── PoiCard.kt           # POI-Karte Composable
│   │   └── WeatherWidget.kt     # Wetter-Widget Composable
│   ├── navigation/
│   │   └── AppNavigation.kt     # NavHost + Screen sealed class
│   ├── screens/
│   │   ├── BookingScreen.kt     # Hotel/Flug-Buchung (Travelpayouts WebView)
│   │   ├── ChatRoomsScreen.kt   # Chat-Raum-Auswahl
│   │   ├── ChatScreen.kt        # Chat (Text + Bilder)
│   │   ├── CoinStoreScreen.kt   # Coin-Shop (IAP)
│   │   ├── ConsentDialog.kt     # DSGVO-Consent-Dialog
│   │   ├── EventsScreen.kt      # Events-Liste
│   │   ├── FriendsScreen.kt     # Freundesliste + Map-Sharing
│   │   ├── HomeScreen.kt        # Startseite
│   │   ├── ImpressumScreen.kt   # Impressum (TMG §5)
│   │   ├── LoginScreen.kt       # Google Sign-In
│   │   ├── MapScreen.kt         # Google Maps + POIs + Events
│   │   ├── NicknameScreen.kt    # Nickname-Auswahl
│   │   ├── PoiDetailScreen.kt   # POI-Detailansicht
│   │   ├── PrivacyPolicyScreen.kt # DSGVO-Datenschutzerklärung
│   │   ├── ProfileScreen.kt     # Profil + Daten löschen
│   │   └── WeatherScreen.kt     # Wetter + 5-Tage-Prognose
│   ├── theme/
│   │   ├── Color.kt             # Farb-Palette
│   │   ├── Theme.kt             # Material3 Theme
│   │   └── Type.kt              # Typografie
│   └── viewmodel/
│       ├── AdViewModel.kt       # Ad-State Management
│       ├── ChatRoomViewModel.kt # Chat-Raum (Input-Sanitization, Rate-Limiting)
│       ├── ChatViewModel.kt     # Chat + Datenlöschung
│       ├── EventsViewModel.kt   # Events
│       ├── HomeViewModel.kt     # Home-Daten
│       ├── MapViewModel.kt      # Karte + Community-Events
│       └── WeatherViewModel.kt  # Wetter
├── util/
│   ├── LocationUtils.kt         # Standort-Hilfsfunktionen
│   └── MessageCleanupWorker.kt  # HiltWorker (90-Tage Cleanup)
├── app/src/main/cpp/             # Vivox C++ JNI Bridge
├── app/src/main/jniLibs/         # Vivox .so Dateien (4 ABIs)
├── app/src/main/res/xml/
│   ├── backup_rules.xml
│   ├── data_extraction_rules.xml
│   ├── file_paths.xml
│   └── network_security_config.xml
├── docs/                         # GitHub Pages (app-ads.txt)
│   ├── index.html
│   └── app-ads.txt
└── proguard-rules.pro            # Log-Stripping + alle SDKs
```

## Anpassung für neue Insel/Destination

### 1. Globale Variablen ändern (gradle.properties)
```properties
# App-Identität
APPLICATION_ID=com.{destination}.insider
APP_NAME={Destination} Insider

# API Keys (eigene beantragen!)
WEATHER_API_KEY=...
MAPS_API_KEY=...
ADMOB_APP_ID=...
ADMOB_BANNER_ID=...
ADMOB_INTERSTITIAL_ID=...
ADMOB_NATIVE_ID=...
ADMOB_APP_OPEN_ID=...
ADMOB_REWARDED_ID=...
UNITY_GAME_ID=...
GOOGLE_CLIENT_ID=...
TRAVELPAYOUTS_LINK=...
VIVOX_SERVER=...
VIVOX_DOMAIN=...
VIVOX_ISSUER=...
VIVOX_KEY=...
APPODEAL_APP_KEY=...
```

### 2. Package umbenennen
- `com.calaratjada.insider` → `com.{destination}.insider`
- In allen `.kt`-Dateien, `AndroidManifest.xml`, `build.gradle.kts`

### 3. SeedData.kt anpassen
- POIs (Strände, Restaurants, Bars, Sehenswürdigkeiten, Shops)
- Events (lokale Feste, Märkte, Konzerte)
- Koordinaten (Maps-Zentrum, Zoom-Level)
- Kategorien (optional neue hinzufügen)

### 4. Theme anpassen (Color.kt, Theme.kt)
- Primärfarben passend zur Destination
- App-Icon + Splash Screen

### 5. Texte lokalisieren
- HomeScreen: Begrüßungstext, Untertitel
- ImpressumScreen: Verantwortlicher aktualisieren
- PrivacyPolicyScreen: App-Name, Verantwortlicher
- BookingScreen: Travelpayouts-Link Destination

### 6. Google Play Console
- Neue App anlegen mit neuem Package Name
- AdMob-Account: Neue App + Ad Units erstellen
- Appodeal: Neue App registrieren, App Key erhalten
- google-services.json: Neues Firebase-Projekt erstellen

### 7. app-ads.txt aktualisieren
- Von Appodeal-Dashboard herunterladen
- AdMob Publisher-ID ersetzen
- Auf GitHub Pages deployen

## Sicherheitsfeatures (bereits integriert)
- API-Keys in `gradle.properties` (nicht im Code)
- SQLCipher-Datenbankverschlüsselung (AES-256)
- EncryptedSharedPreferences für Consent-Daten
- Input-Sanitization (HTML-Entities, Injection-Schutz)
- Rate-Limiting (5 Nachrichten/10 Sekunden)
- URI-Validierung (nur content:// und https://)
- WebView-Hardening (CSP, Domain-Whitelist)
- ProGuard Log-Stripping (d/v/i) im Release
- Network Security Config (HTTPS-only, Appodeal-Domains)
- DSGVO-konform (Consent, Datenlöschung, 90-Tage Retention)

## Coin-Preise
| Paket | Preis | Coins |
|-------|-------|-------|
| Starter | €0,99 | 100 |
| Popular | €3,99 | 500 |
| Best Value | €7,99 | 1.200 |
| Premium | €16,99 | 3.000 |
| Ultimate | €34,99 | 7.500 |
| Ad-Free | €4,99 | - |

## Coin-Kosten
| Aktion | Coins |
|--------|-------|
| Textnachricht | 1 |
| Bildnachricht | 2 |
| Raum freischalten | 5 |
| Map-Event (24h) | 20 |
| Map-Event (7 Tage) | 75 |
