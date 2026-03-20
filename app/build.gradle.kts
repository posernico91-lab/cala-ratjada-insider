plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

// Load API keys from gradle.properties (NOT hardcoded)
fun getSecret(key: String): String = project.findProperty(key) as? String ?: ""

android {
    namespace = "com.calaratjada.insider"
    compileSdk = 35

    signingConfigs {
        create("release") {
            storeFile = file(getSecret("KEYSTORE_FILE").ifEmpty { "../insider-release.jks" })
            storePassword = getSecret("KEYSTORE_PASSWORD")
            keyAlias = getSecret("KEY_ALIAS").ifEmpty { "insider-key" }
            keyPassword = getSecret("KEY_PASSWORD")
        }
    }

    defaultConfig {
        applicationId = "com.calaratjada.insider"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
        }

        buildConfigField("String", "WEATHER_API_KEY", "\"${getSecret("WEATHER_API_KEY")}\"")
        buildConfigField("String", "MAPS_API_KEY", "\"${getSecret("MAPS_API_KEY")}\"")
        buildConfigField("String", "ADMOB_APP_ID", "\"${getSecret("ADMOB_APP_ID")}\"")
        buildConfigField("String", "ADMOB_BANNER_ID", "\"${getSecret("ADMOB_BANNER_ID")}\"")
        buildConfigField("String", "ADMOB_INTERSTITIAL_ID", "\"${getSecret("ADMOB_INTERSTITIAL_ID")}\"")
        buildConfigField("String", "ADMOB_NATIVE_ID", "\"${getSecret("ADMOB_NATIVE_ID")}\"")
        buildConfigField("String", "ADMOB_APP_OPEN_ID", "\"${getSecret("ADMOB_APP_OPEN_ID")}\"")
        buildConfigField("String", "ADMOB_REWARDED_ID", "\"${getSecret("ADMOB_REWARDED_ID")}\"")
        buildConfigField("String", "UNITY_GAME_ID", "\"${getSecret("UNITY_GAME_ID")}\"")
        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"${getSecret("GOOGLE_CLIENT_ID")}\"")
        buildConfigField("String", "TRAVELPAYOUTS_LINK", "\"${getSecret("TRAVELPAYOUTS_LINK")}\"")

        // Vivox
        buildConfigField("String", "VIVOX_SERVER", "\"${getSecret("VIVOX_SERVER")}\"")
        buildConfigField("String", "VIVOX_DOMAIN", "\"${getSecret("VIVOX_DOMAIN")}\"")
        buildConfigField("String", "VIVOX_ISSUER", "\"${getSecret("VIVOX_ISSUER")}\"")
        buildConfigField("String", "VIVOX_KEY", "\"${getSecret("VIVOX_KEY")}\"")

        // Appodeal
        buildConfigField("String", "APPODEAL_APP_KEY", "\"${getSecret("APPODEAL_APP_KEY")}\"")

        resValue("string", "google_maps_key", getSecret("MAPS_API_KEY"))

        manifestPlaceholders["ADMOB_APP_ID"] = getSecret("ADMOB_APP_ID")

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    flavorDimensions += "country"

    productFlavors {
        // 1. DEUTSCHLAND (Master) - Cala Ratjada, Mallorca
        create("germany") {
            dimension = "country"
            applicationId = "com.calaratjada.insider"
            resValue("string", "app_name", "Cala Ratjada Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"DE\"")
        }
        // 2. USA - Miami Beach
        create("usa") {
            dimension = "country"
            applicationId = "com.insider.travel.usa"
            resValue("string", "app_name", "Miami Beach Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"US\"")
        }
        // 3. BRASILIEN - Rio de Janeiro
        create("brazil") {
            dimension = "country"
            applicationId = "com.insider.travel.brazil"
            resValue("string", "app_name", "Rio Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"BR\"")
        }
        // 4. PERU - Cusco
        create("peru") {
            dimension = "country"
            applicationId = "com.insider.travel.peru"
            resValue("string", "app_name", "Cusco Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"PE\"")
        }
        // 5. MEXIKO - Cancún
        create("mexico") {
            dimension = "country"
            applicationId = "com.insider.travel.mexico"
            resValue("string", "app_name", "Cancún Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"MX\"")
        }
        // 6. ENGLAND - London
        create("uk") {
            dimension = "country"
            applicationId = "com.insider.travel.uk"
            resValue("string", "app_name", "London Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"GB\"")
        }
        // 7. ALBANIEN - Sarandë
        create("albania") {
            dimension = "country"
            applicationId = "com.insider.travel.albania"
            resValue("string", "app_name", "Sarandë Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"AL\"")
        }
        // 8. GEORGIEN - Tbilisi
        create("georgia") {
            dimension = "country"
            applicationId = "com.insider.travel.georgia"
            resValue("string", "app_name", "Tbilisi Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"GE\"")
        }
        // 9. SÜDAFRIKA - Cape Town
        create("southafrica") {
            dimension = "country"
            applicationId = "com.insider.travel.southafrica"
            resValue("string", "app_name", "Cape Town Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"ZA\"")
        }
        // 10. NIGERIA - Lagos
        create("nigeria") {
            dimension = "country"
            applicationId = "com.insider.travel.nigeria"
            resValue("string", "app_name", "Lagos Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"NG\"")
        }
        // 11. KENIA - Nairobi
        create("kenya") {
            dimension = "country"
            applicationId = "com.insider.travel.kenya"
            resValue("string", "app_name", "Nairobi Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"KE\"")
        }
        // 12. ÄGYPTEN - Hurghada
        create("egypt") {
            dimension = "country"
            applicationId = "com.insider.travel.egypt"
            resValue("string", "app_name", "Hurghada Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"EG\"")
        }
        // 13. BOTSWANA - Maun
        create("botswana") {
            dimension = "country"
            applicationId = "com.insider.travel.botswana"
            resValue("string", "app_name", "Maun Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"BW\"")
        }
        // 14. KAP VERDE - Santa Maria
        create("capeverde") {
            dimension = "country"
            applicationId = "com.insider.travel.capeverde"
            resValue("string", "app_name", "Cape Verde Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"CV\"")
        }
        // 15. MAROKKO - Marrakech
        create("morocco") {
            dimension = "country"
            applicationId = "com.insider.travel.morocco"
            resValue("string", "app_name", "Marrakech Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"MA\"")
        }
        // 16. TANSANIA - Zanzibar
        create("tanzania") {
            dimension = "country"
            applicationId = "com.insider.travel.tanzania"
            resValue("string", "app_name", "Zanzibar Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"TZ\"")
        }
        // 17. VIETNAM - Ho Chi Minh City
        create("vietnam") {
            dimension = "country"
            applicationId = "com.insider.travel.vietnam"
            resValue("string", "app_name", "Saigon Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"VN\"")
        }
        // 18. BHUTAN - Thimphu
        create("bhutan") {
            dimension = "country"
            applicationId = "com.insider.travel.bhutan"
            resValue("string", "app_name", "Bhutan Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"BT\"")
        }
        // 19. THAILAND - Bangkok
        create("thailand") {
            dimension = "country"
            applicationId = "com.insider.travel.thailand"
            resValue("string", "app_name", "Bangkok Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"TH\"")
        }
        // 20. TADSCHIKISTAN - Dushanbe
        create("tajikistan") {
            dimension = "country"
            applicationId = "com.insider.travel.tajikistan"
            resValue("string", "app_name", "Dushanbe Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"TJ\"")
        }
        // 21. SAUDI-ARABIEN - Jeddah
        create("saudiarabia") {
            dimension = "country"
            applicationId = "com.insider.travel.saudiarabia"
            resValue("string", "app_name", "Jeddah Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"SA\"")
        }
        // 22. DUBAI (UAE)
        create("dubai") {
            dimension = "country"
            applicationId = "com.insider.travel.dubai"
            resValue("string", "app_name", "Dubai Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"AE\"")
        }
        // 23. JAPAN - Tokyo
        create("japan") {
            dimension = "country"
            applicationId = "com.insider.travel.japan"
            resValue("string", "app_name", "Tokyo Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"JP\"")
        }
        // 24. AUSTRALIEN - Sydney
        create("australia") {
            dimension = "country"
            applicationId = "com.insider.travel.australia"
            resValue("string", "app_name", "Sydney Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"AU\"")
        }
        // 25. EUROPA - Barcelona
        create("europe") {
            dimension = "country"
            applicationId = "com.insider.travel.europe"
            resValue("string", "app_name", "Barcelona Insider")
            buildConfigField("String", "COUNTRY_CODE", "\"EU\"")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

dependencies {
    // Vivox SDK AAR
    implementation(files("libs/vivox-sdk.aar"))

    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.09.03")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.navigation:navigation-compose:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")

    // Hilt DI
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coil Image Loading
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Google Maps Compose
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // AdMob + Mediation
    implementation("com.google.android.gms:play-services-ads:23.4.0")

    // Unity Ads SDK + AdMob Mediation Adapter
    implementation("com.unity3d.ads:unity-ads:4.12.4")
    implementation("com.google.ads.mediation:unity:4.12.4.0")

    // Google Auth
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Google Play Billing (In-App Purchases)
    implementation("com.android.billingclient:billing-ktx:7.0.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // WebView
    implementation("androidx.webkit:webkit:1.11.0")

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // === SECURITY ===
    // SQLCipher for encrypted Room database (DSGVO Art. 32)
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")
    implementation("androidx.sqlite:sqlite-ktx:2.4.0")

    // Encrypted SharedPreferences (Secure DataStore alternative)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // WorkManager for scheduled message cleanup (DSGVO Art. 17)
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Google User Messaging Platform (GDPR consent)
    implementation("com.google.android.ump:user-messaging-platform:3.0.0")

    // === APPODEAL SDK + Mediation Adapters ===
    implementation("com.appodeal.ads.sdk:core:4.0.0")
    // BidMachine
    implementation("io.bidmachine:ads.networks.amazon:3.5.0.20")
    implementation("io.bidmachine:ads.networks.mintegral:3.5.0.16")
    implementation("io.bidmachine:ads.networks.my_target:3.5.0.21")
    implementation("io.bidmachine:ads.networks.pangle:3.5.0.15")
    implementation("io.bidmachine:ads.networks.vungle:3.5.0.11")
    // Bidon
    implementation("org.bidon:amazon-adapter:11.1.1.0")
    implementation("org.bidon:applovin-adapter:13.5.1.0")
    implementation("org.bidon:bidmachine-adapter:3.5.0.0")
    implementation("org.bidon:bigoads-adapter:5.6.2.0")
    implementation("org.bidon:chartboost-adapter:9.10.2.0")
    implementation("org.bidon:dtexchange-adapter:8.4.1.0")
    implementation("org.bidon:inmobi-adapter:11.1.0.0")
    implementation("org.bidon:ironsource-adapter:9.1.0.0")
    implementation("org.bidon:meta-adapter:6.20.0.0")
    implementation("org.bidon:mintegral-adapter:17.0.31.0")
    implementation("org.bidon:mobilefuse-adapter:1.9.3.0")
    implementation("org.bidon:moloco-adapter:4.3.1.0")
    implementation("org.bidon:startio-adapter:5.2.4.1")
    implementation("org.bidon:taurusx-adapter:1.12.2.0")
    implementation("org.bidon:unityads-adapter:4.16.4.1")
    implementation("org.bidon:vkads-adapter:5.27.4.0")
    implementation("org.bidon:vungle-adapter:7.6.1.0")
    implementation("org.bidon:yandex-adapter:7.17.0.0")
    // AppLovin MAX
    implementation("com.applovin.mediation:amazon-tam-adapter:11.1.1.0")
    implementation("com.applovin.mediation:bidmachine-adapter:3.5.0.0")
    implementation("com.applovin.mediation:bigoads-adapter:5.6.2.0")
    implementation("com.applovin.mediation:bytedance-adapter:7.7.0.2.0")
    implementation("com.applovin.mediation:chartboost-adapter:9.10.2.0")
    implementation("com.applovin.mediation:facebook-adapter:6.20.0.0")
    implementation("com.applovin.mediation:fyber-adapter:8.4.1.0")
    implementation("com.applovin.mediation:google-ad-manager-adapter:24.7.0.0")
    implementation("com.applovin.mediation:google-adapter:24.7.0.0")
    implementation("com.applovin.mediation:inmobi-adapter:11.1.0.0")
    implementation("com.applovin.mediation:ironsource-adapter:9.1.0.0.0")
    implementation("com.applovin.mediation:mintegral-adapter:17.0.31.0")
    implementation("com.applovin.mediation:mobilefuse-adapter:1.9.3.0")
    implementation("com.applovin.mediation:moloco-adapter:4.3.1.0")
    implementation("com.applovin.mediation:mytarget-adapter:5.27.4.0")
    implementation("com.applovin.mediation:ogury-presage-adapter:6.2.0.0")
    implementation("com.applovin.mediation:pubmatic-adapter:4.10.0.0")
    implementation("com.applovin.mediation:smaato-adapter:22.7.2.3")
    implementation("com.applovin.mediation:unityads-adapter:4.16.4.0")
    implementation("com.applovin.mediation:verve-adapter:3.7.1.0")
    implementation("com.applovin.mediation:vungle-adapter:7.6.1.0")
    implementation("com.applovin.mediation:yandex-adapter:7.17.0.0")
    // Appodeal Adapters
    implementation("com.appodeal.ads.sdk.adapters:adjust:5.4.6.0")
    implementation("com.appodeal.ads.sdk.adapters:admob:24.7.0.0")
    implementation("com.appodeal.ads.sdk.adapters:amazon:11.1.1.0")
    implementation("com.appodeal.ads.sdk.adapters:applovin:13.5.1.0")
    implementation("com.appodeal.ads.sdk.adapters:applovin_max:13.5.1.0")
    implementation("com.appodeal.ads.sdk.adapters:appsflyer:6.17.3.0")
    implementation("com.appodeal.ads.sdk.adapters:bidmachine:3.5.0.0")
    implementation("com.appodeal.ads.sdk.adapters:bidon:0.13.0.0")
    implementation("com.appodeal.ads.sdk.adapters:bigo_ads:5.6.2.0")
    implementation("com.appodeal.ads.sdk.adapters:chartboost:9.10.2.0")
    implementation("com.appodeal.ads.sdk.adapters:dt_exchange:8.4.1.0")
    implementation("com.appodeal.ads.sdk.adapters:facebook_analytics:18.0.3.0")
    implementation("com.appodeal.ads.sdk.adapters:firebase:23.0.0.0")
    implementation("com.appodeal.ads.sdk.adapters:iab:1.8.1.0")
    implementation("com.appodeal.ads.sdk.adapters:inmobi:11.1.0.0")
    implementation("com.appodeal.ads.sdk.adapters:ironsource:9.1.0.0")
    implementation("com.appodeal.ads.sdk.adapters:level_play:9.1.0.0")
    implementation("com.appodeal.ads.sdk.adapters:meta:6.20.0.0")
    implementation("com.appodeal.ads.sdk.adapters:mintegral:17.0.31.0")
    implementation("com.appodeal.ads.sdk.adapters:mobilefuse:1.9.3.0")
    implementation("com.appodeal.ads.sdk.adapters:moloco:4.3.1.0")
    implementation("com.appodeal.ads.sdk.adapters:my_target:5.27.4.0")
    implementation("com.appodeal.ads.sdk.adapters:ogury:6.2.0.0")
    implementation("com.appodeal.ads.sdk.adapters:pangle:7.7.0.2.0")
    implementation("com.appodeal.ads.sdk.adapters:pubmatic:4.10.0.0")
    implementation("com.appodeal.ads.sdk.adapters:sentry_analytics:8.26.0.0")
    implementation("com.appodeal.ads.sdk.adapters:smaato:22.7.2.0")
    implementation("com.appodeal.ads.sdk.adapters:startio:5.2.4.0")
    implementation("com.appodeal.ads.sdk.adapters:taurusx:1.12.2.0")
    implementation("com.appodeal.ads.sdk.adapters:unity_ads:4.16.4.0")
    implementation("com.appodeal.ads.sdk.adapters:verve:3.7.1.0")
    implementation("com.appodeal.ads.sdk.adapters:vungle:7.6.1.0")
    implementation("com.appodeal.ads.sdk.adapters:yandex:7.17.0.0")
    // Level Play
    implementation("com.unity3d.ads-mediation:admob-adapter:5.2.0")
    implementation("com.unity3d.ads-mediation:applovin-adapter:5.2.0")
    implementation("com.unity3d.ads-mediation:bidmachine-adapter:5.1.0")
    implementation("com.unity3d.ads-mediation:bigo-adapter:5.2.0")
    implementation("com.unity3d.ads-mediation:facebook-adapter:5.0.0")
    implementation("com.unity3d.ads-mediation:fyber-adapter:5.1.0")
    implementation("com.unity3d.ads-mediation:inmobi-adapter:5.3.0")
    implementation("com.unity3d.ads-mediation:mintegral-adapter:5.3.0")
    implementation("com.unity3d.ads-mediation:mobilefuse-adapter:5.0.0")
    implementation("com.unity3d.ads-mediation:moloco-adapter:5.5.0")
    implementation("com.unity3d.ads-mediation:mytarget-adapter:5.2.0")
    implementation("com.unity3d.ads-mediation:ogury-adapter:5.1.0")
    implementation("com.unity3d.ads-mediation:pangle-adapter:5.3.0")
    implementation("com.unity3d.ads-mediation:smaato-adapter:5.0.0")
    implementation("com.unity3d.ads-mediation:unityads-adapter:5.3.0")
    implementation("com.unity3d.ads-mediation:verve-adapter:5.2.0")
    implementation("com.unity3d.ads-mediation:vungle-adapter:5.2.0")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
