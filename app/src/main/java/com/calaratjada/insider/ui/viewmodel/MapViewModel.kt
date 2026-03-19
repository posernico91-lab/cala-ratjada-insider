package com.calaratjada.insider.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calaratjada.insider.data.model.MapEvent
import com.calaratjada.insider.data.model.Poi
import com.calaratjada.insider.data.repository.ChatRepository
import com.calaratjada.insider.data.repository.PoiRepository
import com.calaratjada.insider.data.service.CoinManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val poiRepository: PoiRepository,
    private val chatRepository: ChatRepository,
    private val coinManager: CoinManager
) : ViewModel() {

    val allPois: StateFlow<List<Poi>> = poiRepository.getAllPois()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mapEvents: StateFlow<List<MapEvent>> = chatRepository.getActiveMapEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coinBalance = coinManager.coinBalance

    private val _selectedPoi = MutableStateFlow<Poi?>(null)
    val selectedPoi: StateFlow<Poi?> = _selectedPoi.asStateFlow()

    private val _selectedEvent = MutableStateFlow<MapEvent?>(null)
    val selectedEvent: StateFlow<MapEvent?> = _selectedEvent.asStateFlow()

    private val _eventCreationError = MutableSharedFlow<String>(extraBufferCapacity = 5)
    val eventCreationError = _eventCreationError.asSharedFlow()

    fun selectPoi(poi: Poi?) {
        _selectedPoi.value = poi
        _selectedEvent.value = null
    }

    fun selectEvent(event: MapEvent?) {
        _selectedEvent.value = event
        _selectedPoi.value = null
    }

    fun toggleFavorite(poi: Poi) {
        viewModelScope.launch {
            poiRepository.toggleFavorite(poi.id, !poi.isFavorite)
        }
    }

    fun createMapEvent(
        title: String,
        description: String,
        lat: Double,
        lng: Double,
        durationDays: Int,
        creatorName: String
    ) {
        val cost = if (durationDays <= 1) CoinManager.COST_MAP_EVENT_24H else CoinManager.COST_MAP_EVENT_7D
        viewModelScope.launch {
            val success = coinManager.spendCoins(cost, "spend_map_event", "Map-Event erstellt ($durationDays Tage)")
            if (success) {
                val expiresAt = System.currentTimeMillis() + durationDays * 24 * 60 * 60 * 1000L
                val event = MapEvent(
                    title = title,
                    description = description,
                    lat = lat,
                    lng = lng,
                    creatorUri = "",
                    creatorName = creatorName,
                    expiresAt = expiresAt
                )
                chatRepository.createMapEvent(event)
            } else {
                _eventCreationError.emit("Du brauchst $cost Coins für ein $durationDays-Tage-Event")
            }
        }
    }
}
