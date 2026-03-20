# ProGuard rules for Cala Ratjada Insider

# === SECURITY: Strip debug logs in release ===
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson
-keepattributes Signature
-keep class com.calaratjada.insider.data.remote.** { *; }
-keep class com.calaratjada.insider.data.model.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Hilt
-keep class dagger.hilt.** { *; }

# Google Maps
-keep class com.google.android.gms.maps.** { *; }

# Coil
-keep class coil.** { *; }

# Vivox JNI Bridge - NEVER obfuscate
-keep class com.calaratjada.insider.data.service.VivoxNative { *; }
-keepclasseswithmembernames class com.calaratjada.insider.data.service.VivoxNative {
    native <methods>;
}

# Auth & Token generation
-keep class com.calaratjada.insider.data.service.AuthService { *; }
-keep class com.calaratjada.insider.data.service.VivoxTokenGenerator { *; }

# Google Auth
-keep class com.google.android.gms.auth.** { *; }

# Billing
-keep class com.android.billingclient.** { *; }

# SQLCipher
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }

# Security Crypto
-keep class androidx.security.crypto.** { *; }

# Unity Ads
-keep class com.unity3d.ads.** { *; }

# Appodeal SDK & Mediation
-keep class com.appodeal.** { *; }
-keep class com.explorestack.** { *; }
-keep class io.bidmachine.** { *; }
-keep class org.bidon.** { *; }
-dontwarn com.appodeal.**
-dontwarn io.bidmachine.**
-dontwarn org.bidon.**

# OK Tracer (Appodeal dependency) - fix R8 ConcurrentModificationException
-keep class ru.ok.tracer.** { *; }
-dontwarn ru.ok.tracer.**

# Appodeal mediation service files
-keep class META-INF.services.** { *; }
-dontwarn META-INF.services.**

# Fix R8 8.5.x ConcurrentModificationException in service loader processing
-adaptresourcefilecontents META-INF/services/*
-dontwarn javax.annotation.**
-dontwarn org.codehaus.**
-dontwarn com.squareup.moshi.**

# WorkManager
-keep class androidx.work.** { *; }
