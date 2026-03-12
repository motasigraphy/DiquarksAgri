package com.diquarks.diquarksagri.network

data class WeatherResponse(
    val location: Location,
    val current: Current
)

data class Location(
    val name: String,
    val country: String
)

data class Current(
    val temp_c: Double,
    val humidity: Int,
    val wind_kph: Double,
    val condition: Condition
)

data class Condition(
    val text: String,
    val icon: String
)