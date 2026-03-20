package com.calaratjada.insider.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.calaratjada.insider.config.ActiveCountryConfig
import com.calaratjada.insider.ui.theme.*
import com.calaratjada.insider.ui.components.AdBanner
import com.calaratjada.insider.ui.viewmodel.WeatherUiState
import com.calaratjada.insider.ui.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Emerald500,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Wetter",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = ActiveCountryConfig.primaryCity,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Ad Banner
        item {
            AdBanner(modifier = Modifier.padding(vertical = 8.dp))
        }

        when {
            uiState.isLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Emerald500)
                    }
                }
            }

            uiState.error != null -> {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = Stone400,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Wetterdaten konnten nicht geladen werden",
                            color = Stone500
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadWeather() }) {
                            Text("Erneut versuchen")
                        }
                    }
                }
            }

            uiState.weather != null -> {
                val weather = uiState.weather!!

                // Current weather
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = weather.iconUrl,
                                contentDescription = weather.description,
                                modifier = Modifier.size(100.dp),
                                contentScale = ContentScale.Fit
                            )
                            Text(
                                text = "${weather.temperature.toInt()}°C",
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Black,
                                color = Stone900
                            )
                            Text(
                                text = weather.description,
                                fontSize = 18.sp,
                                color = Stone600,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Gefühlt wie ${weather.feelsLike.toInt()}°C",
                                fontSize = 13.sp,
                                color = Stone400
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Details row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                WeatherDetailItem(
                                    icon = Icons.Default.Thermostat,
                                    label = "Min/Max",
                                    value = "${weather.minTemp.toInt()}° / ${weather.maxTemp.toInt()}°"
                                )
                                WeatherDetailItem(
                                    icon = Icons.Default.WaterDrop,
                                    label = "Feuchtigkeit",
                                    value = "${weather.humidity}%"
                                )
                                WeatherDetailItem(
                                    icon = Icons.Default.Air,
                                    label = "Wind",
                                    value = "${weather.windSpeed} m/s"
                                )
                            }
                        }
                    }
                }

                // Forecast
                if (uiState.forecast.isNotEmpty()) {
                    item {
                        Text(
                            text = "5-Tage Vorhersage",
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    items(uiState.forecast) { day ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = day.iconUrl,
                                    contentDescription = day.description,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = day.date,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Stone900
                                    )
                                    Text(
                                        text = day.description,
                                        fontSize = 12.sp,
                                        color = Stone500
                                    )
                                }
                                Text(
                                    text = "${day.tempMin.toInt()}° / ${day.tempMax.toInt()}°",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Stone800
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherDetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Emerald500,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Stone800)
        Text(text = label, fontSize = 10.sp, color = Stone400)
    }
}
