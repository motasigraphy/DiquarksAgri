package com.diquarks.diquarksagri.network

data class WeatherResponse(
    val location: Location,
    val current: Current,
    val forecast: Forecast
)

data class Location(
    val name: String,
    val country: String
)

data class Current(
    val temp_c: Double,
    val humidity: Int,
    val wind_kph: Double,
    val is_day: Int,
    val condition: Condition
)

data class Condition(
    val text: String,
    val icon: String
)

// 🔥 FORECAST PART
data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Day
)

data class Day(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val condition: Condition
)