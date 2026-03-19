package com.calaratjada.insider.data.model

data class WeatherInfo(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val description: String,
    val icon: String,
    val windSpeed: Double,
    val cityName: String,
    val minTemp: Double,
    val maxTemp: Double
) {
    val iconUrl: String
        get() = "https://openweathermap.org/img/wn/${icon}@4x.png"
}

data class ForecastDay(
    val date: String,
    val tempMin: Double,
    val tempMax: Double,
    val icon: String,
    val description: String
) {
    val iconUrl: String
        get() = "https://openweathermap.org/img/wn/${icon}@2x.png"
}
