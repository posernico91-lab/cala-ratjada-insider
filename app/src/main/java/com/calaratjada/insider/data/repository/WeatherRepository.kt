package com.calaratjada.insider.data.repository

import com.calaratjada.insider.BuildConfig
import com.calaratjada.insider.config.ActiveCountryConfig
import com.calaratjada.insider.data.model.ForecastDay
import com.calaratjada.insider.data.model.WeatherInfo
import com.calaratjada.insider.data.remote.WeatherApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi
) {
    suspend fun getCurrentWeather(): Result<WeatherInfo> = runCatching {
        val response = weatherApi.getCurrentWeather(
            lat = ActiveCountryConfig.lat,
            lon = ActiveCountryConfig.lng,
            apiKey = BuildConfig.WEATHER_API_KEY,
            lang = ActiveCountryConfig.defaultLocale.take(2)
        )
        val weather = response.weather.first()
        WeatherInfo(
            temperature = response.main.temp,
            feelsLike = response.main.feelsLike,
            humidity = response.main.humidity,
            description = weather.description.replaceFirstChar { it.uppercase() },
            icon = weather.icon,
            windSpeed = response.wind.speed,
            cityName = response.name,
            minTemp = response.main.tempMin,
            maxTemp = response.main.tempMax
        )
    }

    suspend fun getForecast(): Result<List<ForecastDay>> = runCatching {
        val response = weatherApi.getForecast(
            lat = ActiveCountryConfig.lat,
            lon = ActiveCountryConfig.lng,
            apiKey = BuildConfig.WEATHER_API_KEY,
            lang = ActiveCountryConfig.defaultLocale.take(2)
        )
        response.list
            .filter { it.dtTxt.contains("12:00:00") }
            .take(5)
            .map { item ->
                val weather = item.weather.first()
                ForecastDay(
                    date = item.dtTxt.substringBefore(" "),
                    tempMin = item.main.tempMin,
                    tempMax = item.main.tempMax,
                    icon = weather.icon,
                    description = weather.description.replaceFirstChar { it.uppercase() }
                )
            }
    }
}
