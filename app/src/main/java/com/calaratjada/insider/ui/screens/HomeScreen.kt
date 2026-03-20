package com.calaratjada.insider.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.calaratjada.insider.R
import com.calaratjada.insider.config.ActiveCountryConfig
import com.calaratjada.insider.data.model.Category
import com.calaratjada.insider.ui.components.AdBanner
import com.calaratjada.insider.ui.components.CategoryChip
import com.calaratjada.insider.ui.components.PoiCard
import com.calaratjada.insider.ui.components.WeatherWidget
import com.calaratjada.insider.ui.theme.*
import com.calaratjada.insider.ui.viewmodel.HomeViewModel
import com.calaratjada.insider.ui.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPoiClick: (String) -> Unit,
    onNavigateToWeather: () -> Unit,
    onNavigateToBooking: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    weatherViewModel: WeatherViewModel = hiltViewModel()
) {
    val pois by homeViewModel.pois.collectAsStateWithLifecycle()
    val selectedCategory by homeViewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by homeViewModel.searchQuery.collectAsStateWithLifecycle()
    val showFavoritesOnly by homeViewModel.showFavoritesOnly.collectAsStateWithLifecycle()
    val weatherState by weatherViewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Logo",
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = ActiveCountryConfig.primaryCity.uppercase(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    fontStyle = FontStyle.Italic,
                                    letterSpacing = (-1).sp,
                                    color = Stone900
                                )
                                Text(
                                    text = "INSIDER GUIDE",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    color = Emerald600,
                                    letterSpacing = 3.sp
                                )
                            }
                        }
                        IconButton(
                            onClick = { homeViewModel.toggleFavoritesOnly() }
                        ) {
                            Icon(
                                imageVector = if (showFavoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favoriten",
                                tint = if (showFavoritesOnly) Red500 else Stone400
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { homeViewModel.setSearchQuery(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Suchen…", color = Stone400) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Stone400)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { homeViewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Löschen")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Emerald500,
                            unfocusedBorderColor = Stone200,
                            focusedContainerColor = Stone50,
                            unfocusedContainerColor = Stone100
                        )
                    )
                }
            }
        }

        // Weather widget
        item {
            val weather = weatherState.weather
            if (weather != null) {
                WeatherWidget(
                    weather = weather,
                    onClick = onNavigateToWeather,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }
        }

        // Categories
        item {
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryChip(
                        category = null,
                        isSelected = selectedCategory == null && !showFavoritesOnly,
                        onClick = { homeViewModel.selectCategory(null) }
                    )
                }
                items(Category.entries.toList()) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = selectedCategory == category,
                        onClick = { homeViewModel.selectCategory(category) }
                    )
                }
            }
        }

        // Booking quick action
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Stone900),
                onClick = onNavigateToBooking
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Flight,
                        contentDescription = null,
                        tint = Emerald400,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reise planen",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Flüge, Hotels & Mietwagen",
                            color = Stone400,
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Stone400
                    )
                }
            }
        }

        // Ad Banner
        item {
            AdBanner(modifier = Modifier.padding(vertical = 8.dp))
        }

        // Section header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showFavoritesOnly) "Meine Favoriten" else "Empfehlungen",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${pois.size} Orte",
                    style = MaterialTheme.typography.bodySmall,
                    color = Stone400
                )
            }
        }

        // POI list
        if (pois.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Keine Orte gefunden.",
                        color = Stone400,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(pois, key = { it.id }) { poi ->
                PoiCard(
                    poi = poi,
                    onClick = { onPoiClick(poi.id) },
                    onFavoriteClick = { homeViewModel.toggleFavorite(poi) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
        }
    }
}
