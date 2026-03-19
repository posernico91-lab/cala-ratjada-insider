package com.calaratjada.insider.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.calaratjada.insider.data.model.Category
import com.calaratjada.insider.data.model.MapEvent
import com.calaratjada.insider.ui.theme.*
import com.calaratjada.insider.ui.viewmodel.MapViewModel
import com.calaratjada.insider.util.LocationUtils
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    initialPoiId: String? = null,
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val pois by viewModel.allPois.collectAsStateWithLifecycle()
    val selectedPoi by viewModel.selectedPoi.collectAsStateWithLifecycle()
    val mapEvents by viewModel.mapEvents.collectAsStateWithLifecycle()
    val selectedEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()
    val coinBalance by viewModel.coinBalance.collectAsStateWithLifecycle()

    var showCreateEventDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val calaRatjada = LatLng(LocationUtils.DEFAULT_LAT, LocationUtils.DEFAULT_LNG)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(calaRatjada, 14f)
    }

    // Listen for event creation errors
    LaunchedEffect(Unit) {
        viewModel.eventCreationError.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Select initial POI if provided
    LaunchedEffect(initialPoiId, pois) {
        if (initialPoiId != null && pois.isNotEmpty()) {
            val poi = pois.find { it.id == initialPoiId }
            if (poi != null) {
                viewModel.selectPoi(poi)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                    LatLng(poi.lat, poi.lng), 16f
                )
            }
        }
    }

    // Create Event Dialog
    if (showCreateEventDialog) {
        CreateEventDialog(
            coinBalance = coinBalance,
            mapCenter = cameraPositionState.position.target,
            onDismiss = { showCreateEventDialog = false },
            onCreate = { title, desc, lat, lng, days, name ->
                viewModel.createMapEvent(title, desc, lat, lng, days, name)
                showCreateEventDialog = false
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateEventDialog = true },
                containerColor = Emerald500,
                contentColor = Color.White,
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                Icon(Icons.Default.AddLocation, contentDescription = "Event erstellen")
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = false,
                    mapType = MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled = false
                )
            ) {
                // POI markers
                pois.forEach { poi ->
                    val isSelected = selectedPoi?.id == poi.id
                    val markerColor = when (poi.category) {
                        "beach" -> BitmapDescriptorFactory.HUE_CYAN
                        "gastronomy" -> BitmapDescriptorFactory.HUE_ORANGE
                        "nightlife" -> BitmapDescriptorFactory.HUE_VIOLET
                        "shopping" -> BitmapDescriptorFactory.HUE_ROSE
                        "culture" -> BitmapDescriptorFactory.HUE_YELLOW
                        "service" -> BitmapDescriptorFactory.HUE_BLUE
                        else -> BitmapDescriptorFactory.HUE_GREEN
                    }

                    Marker(
                        state = MarkerState(position = LatLng(poi.lat, poi.lng)),
                        title = poi.name,
                        snippet = Category.fromString(poi.category)?.displayName ?: poi.category,
                        icon = BitmapDescriptorFactory.defaultMarker(markerColor),
                        alpha = if (isSelected) 1f else 0.8f,
                        onClick = {
                            viewModel.selectPoi(poi)
                            false
                        }
                    )
                }

                // Map Event markers (red markers)
                mapEvents.forEach { event ->
                    Marker(
                        state = MarkerState(position = LatLng(event.lat, event.lng)),
                        title = event.title,
                        snippet = "von ${event.creatorName}",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                        onClick = {
                            viewModel.selectEvent(event)
                            false
                        }
                    )
                }
            }

            // Selected Map Event card
            selectedEvent?.let { event ->
                MapEventCard(
                    event = event,
                    onClose = { viewModel.selectEvent(null) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(start = 16.dp, end = 16.dp, bottom = 100.dp)
                )
            }

            // Selected POI info card
            selectedPoi?.let { poi ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(start = 16.dp, end = 16.dp, bottom = 100.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = poi.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Stone900
                                )
                                Text(
                                    text = Category.fromString(poi.category)?.displayName ?: poi.category,
                                    fontSize = 12.sp,
                                    color = Emerald500,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = { viewModel.toggleFavorite(poi) },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = if (poi.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorit",
                                        tint = if (poi.isFavorite) Red500 else Stone400
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.selectPoi(null) },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Schließen", tint = Stone400)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = poi.description,
                            fontSize = 13.sp,
                            color = Stone500,
                            lineHeight = 18.sp,
                            maxLines = 3
                        )

                        if (poi.address.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, null, tint = Stone400, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = poi.address, fontSize = 11.sp, color = Stone400)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val uri = Uri.parse("google.navigation:q=${poi.lat},${poi.lng}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                                    setPackage("com.google.android.apps.maps")
                                }
                                if (mapIntent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(mapIntent)
                                } else {
                                    val webUri = Uri.parse(
                                        "https://www.google.com/maps/dir/?api=1&destination=${poi.lat},${poi.lng}"
                                    )
                                    context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
                        ) {
                            Icon(Icons.Default.Navigation, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Bring mich hin", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MapEventCard(
    event: MapEvent,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("dd.MM. HH:mm", Locale.getDefault()) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, null, tint = Red500, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(event.title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Stone900)
                }
                IconButton(onClick = onClose, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Close, "Schließen", tint = Stone400)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(event.description, fontSize = 13.sp, color = Stone600, lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, tint = Stone400, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("von ${event.creatorName}", fontSize = 11.sp, color = Stone400)
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Default.Schedule, null, tint = Stone400, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("bis ${dateFormat.format(Date(event.expiresAt))}", fontSize = 11.sp, color = Stone400)
            }
        }
    }
}

@Composable
private fun CreateEventDialog(
    coinBalance: Int,
    mapCenter: LatLng,
    onDismiss: () -> Unit,
    onCreate: (title: String, desc: String, lat: Double, lng: Double, days: Int, creatorName: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var creatorName by remember { mutableStateOf("") }
    var selectedDays by remember { mutableIntStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.AddLocation, null, tint = Emerald500) },
        title = { Text("Map-Event erstellen", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titel") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Beschreibung") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = creatorName,
                    onValueChange = { creatorName = it },
                    label = { Text("Dein Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Dauer & Kosten:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedDays == 1,
                        onClick = { selectedDays = 1 },
                        label = { Text("24h — 20 Coins") }
                    )
                    FilterChip(
                        selected = selectedDays == 7,
                        onClick = { selectedDays = 7 },
                        label = { Text("7 Tage — 75 Coins") }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonetizationOn, null, tint = Amber500, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Dein Guthaben: $coinBalance Coins", fontSize = 12.sp, color = Stone500)
                }

                Text(
                    "📍 Position: Kartenmitte wird verwendet",
                    fontSize = 11.sp,
                    color = Stone400
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank() && creatorName.isNotBlank()) {
                        onCreate(title.trim(), description.trim(), mapCenter.latitude, mapCenter.longitude, selectedDays, creatorName.trim())
                    }
                },
                enabled = title.isNotBlank() && description.isNotBlank() && creatorName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
            ) {
                Text("Erstellen (${if (selectedDays == 1) 20 else 75} Coins)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}
