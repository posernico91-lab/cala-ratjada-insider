package com.calaratjada.insider.util

import com.calaratjada.insider.config.ActiveCountryConfig
import kotlin.math.*

object LocationUtils {

    /** Center coordinates for active country */
    val DEFAULT_LAT: Double get() = ActiveCountryConfig.lat
    val DEFAULT_LNG: Double get() = ActiveCountryConfig.lng

    /**
     * Calculate distance in km between two coordinates using Haversine formula.
     */
    fun calculateDistance(
        lat1: Double, lng1: Double,
        lat2: Double, lng2: Double
    ): Double {
        val earthRadius = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    fun formatDistance(distanceKm: Double): String =
        if (distanceKm < 1.0) {
            "${(distanceKm * 1000).toInt()} m"
        } else {
            "%.1f km".format(distanceKm)
        }
}
