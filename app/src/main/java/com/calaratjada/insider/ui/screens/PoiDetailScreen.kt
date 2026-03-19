package com.calaratjada.insider.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.calaratjada.insider.data.model.Category
import com.calaratjada.insider.data.model.Poi
import com.calaratjada.insider.data.repository.PoiRepository
import com.calaratjada.insider.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import javax.inject.Inject

@HiltViewModel
class PoiDetailViewModel @Inject constructor(
    private val poiRepository: PoiRepository
) : ViewModel() {
    private val _poi = MutableStateFlow<Poi?>(null)
    val poi: StateFlow<Poi?> = _poi.asStateFlow()

    fun loadPoi(id: String) {
        viewModelScope.launch {
            _poi.value = poiRepository.getPoiById(id)
        }
    }

    fun toggleFavorite() {
        val current = _poi.value ?: return
        viewModelScope.launch {
            poiRepository.toggleFavorite(current.id, !current.isFavorite)
            _poi.value = current.copy(isFavorite = !current.isFavorite)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoiDetailScreen(
    poiId: String,
    onBack: () -> Unit,
    onNavigateToMap: (String) -> Unit,
    viewModel: PoiDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val poi by viewModel.poi.collectAsStateWithLifecycle()

    LaunchedEffect(poiId) {
        viewModel.loadPoi(poiId)
    }

    val currentPoi = poi
    if (currentPoi == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Emerald500)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Image + Back button
        Box {
            AsyncImage(
                model = currentPoi.imageUrl,
                contentDescription = currentPoi.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentScale = ContentScale.Crop
            )

            // Back button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Zurück",
                        modifier = Modifier.padding(8.dp),
                        tint = Stone900
                    )
                }
            }

            // Favorite button
            IconButton(
                onClick = { viewModel.toggleFavorite() },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Icon(
                        imageVector = if (currentPoi.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorit",
                        modifier = Modifier.padding(8.dp),
                        tint = if (currentPoi.isFavorite) Red500 else Stone400
                    )
                }
            }

            // Rating
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.95f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Amber400,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "%.1f".format(currentPoi.rating),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Stone900
                    )
                }
            }
        }

        // Content
        Column(modifier = Modifier.padding(20.dp)) {
            // Category badge
            val category = Category.fromString(currentPoi.category)
            if (category != null) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Emerald50
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            tint = Emerald500,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = category.displayName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald600
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = currentPoi.name,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = Stone900
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = currentPoi.description,
                fontSize = 15.sp,
                color = Stone600,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Info items
            if (currentPoi.address.isNotEmpty()) {
                InfoRow(
                    icon = Icons.Default.LocationOn,
                    text = currentPoi.address
                )
            }
            if (currentPoi.phone.isNotEmpty()) {
                InfoRow(
                    icon = Icons.Default.Phone,
                    text = currentPoi.phone,
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${currentPoi.phone}")
                        }
                        context.startActivity(intent)
                    }
                )
            }
            if (currentPoi.website.isNotEmpty()) {
                InfoRow(
                    icon = Icons.Default.Language,
                    text = currentPoi.website,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentPoi.website))
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Navigate button
                Button(
                    onClick = {
                        val uri = Uri.parse("google.navigation:q=${currentPoi.lat},${currentPoi.lng}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            val webUri = Uri.parse(
                                "https://www.google.com/maps/dir/?api=1&destination=${currentPoi.lat},${currentPoi.lng}"
                            )
                            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Bring mich hin", fontWeight = FontWeight.Bold)
                }

                // Show on map
                OutlinedButton(
                    onClick = { onNavigateToMap(currentPoi.id) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Karte", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Emerald500,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        if (onClick != null) {
            TextButton(onClick = onClick, contentPadding = PaddingValues(0.dp)) {
                Text(text = text, fontSize = 14.sp, color = Emerald600)
            }
        } else {
            Text(text = text, fontSize = 14.sp, color = Stone600)
        }
    }
}
