package com.calaratjada.insider.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calaratjada.insider.data.model.Category
import com.calaratjada.insider.data.model.Poi
import com.calaratjada.insider.data.repository.PoiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val poiRepository: PoiRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val pois: StateFlow<List<Poi>> = combine(
        _selectedCategory,
        _searchQuery,
        _showFavoritesOnly
    ) { category, query, favOnly ->
        Triple(category, query, favOnly)
    }.flatMapLatest { (category, query, favOnly) ->
        when {
            favOnly -> poiRepository.getFavorites()
            query.isNotBlank() -> poiRepository.searchPois(query)
            category != null -> poiRepository.getPoisByCategory(category.name.lowercase())
            else -> poiRepository.getAllPois()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            poiRepository.seedIfEmpty()
        }
    }

    fun selectCategory(category: Category?) {
        _selectedCategory.value = category
        _showFavoritesOnly.value = false
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavoritesOnly() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
        if (_showFavoritesOnly.value) {
            _selectedCategory.value = null
        }
    }

    fun toggleFavorite(poi: Poi) {
        viewModelScope.launch {
            poiRepository.toggleFavorite(poi.id, !poi.isFavorite)
        }
    }
}
