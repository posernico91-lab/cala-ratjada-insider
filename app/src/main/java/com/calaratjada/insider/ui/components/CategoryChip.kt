package com.calaratjada.insider.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calaratjada.insider.data.model.Category
import com.calaratjada.insider.ui.theme.*

@Composable
fun CategoryChip(
    category: Category?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (category != null) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = category?.displayName ?: "Alle",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        selected = isSelected,
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            labelColor = Stone500,
            selectedContainerColor = if (category == null) Stone900 else Emerald500,
            selectedLabelColor = Color.White,
            selectedLeadingIconColor = Color.White
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = Stone200,
            selectedBorderColor = Color.Transparent,
            enabled = true,
            selected = isSelected
        )
    )
}
