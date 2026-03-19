package com.calaratjada.insider.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class Category(
    val displayName: String,
    val icon: ImageVector
) {
    BEACH("Strände", Icons.Default.WbSunny),
    GASTRONOMY("Gastronomie", Icons.Default.Restaurant),
    NIGHTLIFE("Nightlife", Icons.Default.MusicNote),
    SHOPPING("Shopping", Icons.Default.ShoppingBag),
    CULTURE("Kultur", Icons.Default.AccountBalance),
    SERVICE("Service", Icons.Default.Info);

    companion object {
        fun fromString(value: String): Category? =
            entries.find { it.name.equals(value, ignoreCase = true) }
    }
}
