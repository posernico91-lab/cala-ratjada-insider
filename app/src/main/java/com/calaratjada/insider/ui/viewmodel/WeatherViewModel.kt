package com.calaratjada.insider.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calaratjada.insider.data.model.ForecastDay
import com.calaratjada.insider.data.model.WeatherInfo
import com.calaratjada.insider.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeatherUiState(
    val isLoading: Boolean = true,
    val weather: WeatherInfo? = null,
    val forecast: List<ForecastDay> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        loadWeather()
    }

    fun loadWeather() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState(isLoading = true)

            val weatherResult = weatherRepository.getCurrentWeather()
            val forecastResult = weatherRepository.getForecast()

            _uiState.value = WeatherUiState(
                isLoading = false,
                weather = weatherResult.getOrNull(),
                forecast = forecastResult.getOrDefault(emptyList()),
                error = weatherResult.exceptionOrNull()?.message
            )
        }
    }
}
