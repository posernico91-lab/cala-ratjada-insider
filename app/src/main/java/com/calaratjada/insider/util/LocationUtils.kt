package com.calaratjada.insider.util

import android.location.Location
import kotlin.math.*

object LocationUtils {

    /** Cala Ratjada center coordinates */
    const val DEFAULT_LAT = 39.7089
    const val DEFAULT_LNG = 3.4589

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
