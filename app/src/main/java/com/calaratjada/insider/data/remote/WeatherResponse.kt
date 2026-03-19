package com.calaratjada.insider.data.remote

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val main: MainData,
    val weather: List<WeatherData>,
    val wind: WindData,
    val name: String
)

data class MainData(
    val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    val humidity: Int,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double
)

data class WeatherData(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class WindData(
    val speed: Double
)

data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: CityData
)

data class ForecastItem(
    val dt: Long,
    @SerializedName("dt_txt") val dtTxt: String,
    val main: MainData,
    val weather: List<WeatherData>
)

data class CityData(
    val name: String
)
