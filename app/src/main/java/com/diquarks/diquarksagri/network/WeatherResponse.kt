package com.diquarks.diquarksagri.network

data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val name: String
)

data class Weather(
    val main: String,
    val description: String
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class Wind(
    val speed: Double
)