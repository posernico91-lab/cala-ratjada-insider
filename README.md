# Cala Ratjada Insider - Android App

Vollständige native Android-App als Insider-Reiseführer für Cala Ratjada, Mallorca.

## Technologie-Stack

- **Sprache:** Kotlin 2.0
- **UI:** Jetpack Compose + Material3
- **Architektur:** MVVM mit Hilt DI
- **Datenbank:** Room (SQLite) für Offline-Caching
- **Networking:** Retrofit2 + OkHttp
- **Bilder:** Coil
- **Karten:** Google Maps Compose
- **Ads:** Google AdMob
- **Navigation:** Navigation Compose
- **Buchung:** Travelpayouts WebView-Widgets

## Projektstruktur

```
app/src/main/java/com/calaratjada/insider/
├── CalaRatjadaApp.kt          # Application-Klasse (Hilt)
├── MainActivity.kt             # Einstiegspunkt
├── data/
│   ├── model/                  # Datenmodelle (Poi, Event, Category, Weather)
│   ├── local/                  # Room Database + DAOs
│   ├── remote/                 # Retrofit API (Weather)
│   ├── repository/             # Repositories
│   └── SeedData.kt            # Initiale POI- und Event-Daten
├── di/
│   └── AppModule.kt           # Hilt DI-Modul
├── ui/
│   ├── theme/                  # Material3 Theme (Color, Type, Theme)
│   ├── navigation/             # App-Navigation mit BottomNav
│   ├── components/             # Wiederverwendbare UI-Komponenten
│   ├── screens/                # Alle Screens (Home, Map, Weather, Events, Booking, Detail)
│   └── viewmodel/              # ViewModels
└── util/
    └── LocationUtils.kt       # GPS-/Distanzberechnung
```

## Features

- **20+ POIs** in 6 Kategorien (Strände, Gastronomie, Nightlife, Shopping, Kultur, Service)
- **8 Events** im Event-Kalender
- **Google Maps** mit farbcodierten Markern je Kategorie
- **Echtzeit-Wetter** via OpenWeatherMap API (inkl. 5-Tage Vorhersage)
- **Buchungsportal** mit Travelpayouts-Widgets (Flüge, Mietwagen, Touren)
- **Offline-Modus** durch lokale Room-Database
- **"Bring mich hin"** Navigation via Google Maps Intent
- **Favoriten-System** mit Room-Persistenz
- **AdMob Banner-Ads** Integration
- **Suchfunktion** über alle POIs

## Voraussetzungen

1. **Android Studio** (Ladybug oder neuer) oder VS Code mit Android-Erweiterungen
2. **JDK 17** (z.B. OpenJDK oder Oracle JDK)
3. **Android SDK** (API Level 35)
4. **Gradle 8.7** (wird automatisch heruntergeladen)

## Setup

### 1. Gradle Wrapper initialisieren

Falls der `gradle-wrapper.jar` fehlt, in Android Studio öffnen oder manuell generieren:

```bash
cd CalaRatjadaInsider
gradle wrapper --gradle-version 8.7
```

### 2. API-Keys konfigurieren

Die API-Keys sind in `app/build.gradle.kts` als `BuildConfig`-Felder definiert.
Für Produktion sollten diese in `local.properties` verschoben werden.

### 3. AdMob konfigurieren

Ersetze die Platzhalter `XXXXXXXXXX` in folgenden Dateien durch echte AdMob Unit-IDs:
- `app/build.gradle.kts` → `ADMOB_APP_ID` und `ADMOB_BANNER_ID`
- `AndroidManifest.xml` → `<meta-data>` für `APPLICATION_ID`

## APK generieren (Debug)

### Über Android Studio
1. Öffne den `CalaRatjadaInsider`-Ordner in Android Studio
2. Warte bis Gradle synchronisiert ist
3. **Build → Build Bundle(s) / APK(s) → Build APK(s)**
4. Die APK liegt unter: `app/build/outputs/apk/debug/app-debug.apk`

### Über Terminal / VS Code

```bash
# In den Projektordner wechseln
cd CalaRatjadaInsider

# Gradle Wrapper ausführbar machen (Linux/Mac)
chmod +x gradlew

# Debug-APK bauen
./gradlew assembleDebug        # Linux/Mac
gradlew.bat assembleDebug      # Windows
```

Die generierte APK befindet sich unter:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Release-APK

Für eine signierte Release-APK:

```bash
# Keystore generieren (einmalig)
keytool -genkey -v -keystore calaratjada-release.keystore -alias calaratjada -keyalg RSA -keysize 2048 -validity 10000

# In local.properties eintragen:
# RELEASE_STORE_FILE=../calaratjada-release.keystore
# RELEASE_STORE_PASSWORD=dein_passwort
# RELEASE_KEY_ALIAS=calaratjada
# RELEASE_KEY_PASSWORD=dein_passwort

# Release bauen
gradlew.bat assembleRelease
```

## Integrierte APIs & Services

| Service | Zweck | Key/ID |
|---------|-------|--------|
| OpenWeatherMap | Echtzeit-Wetter | `e4b1c1...` |
| Google Maps SDK | Kartenansicht | `AIzaSyC...` |
| Google AdMob | Monetarisierung | `pub-377...` |
| Google Sign-In | Authentifizierung | `831703...` |
| Travelpayouts | Buchungswidgets | `trip.tpx.gr/...` |

## Lizenz

Proprietär - Alle Rechte vorbehalten.
