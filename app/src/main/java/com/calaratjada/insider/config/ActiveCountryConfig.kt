package com.calaratjada.insider.config

import com.calaratjada.insider.BuildConfig
import com.insider.travel.config.Countries
import com.insider.travel.config.CountryConfig

/**
 * Provides the active CountryConfig based on the current BuildConfig.COUNTRY_CODE.
 * Used throughout the app to replace hardcoded destination-specific values.
 */
object ActiveCountryConfig {

    val config: CountryConfig by lazy {
        Countries.getByCode(BuildConfig.COUNTRY_CODE)
            ?: Countries.all.first() // fallback to Germany/Cala Ratjada
    }

    val appName: String get() = config.appName
    val primaryCity: String get() = config.primaryCity
    val countryName: String get() = config.countryName
    val lat: Double get() = config.lat
    val lng: Double get() = config.lng
    val mapZoom: Float get() = config.mapZoom
    val currencyCode: String get() = config.currencyCode
    val currencySymbol: String get() = config.currencySymbol
    val defaultLocale: String get() = config.defaultLocale
    val legalFramework: String get() = config.legalFramework
    val timezone: String get() = config.timezone
}
