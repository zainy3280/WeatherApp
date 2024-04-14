package com.example.weatherapp

data class WeatherForecast(
    val city: City,
    val cnt: Int,
    val cod: String,
    val message: Int,
    val list: List<String>,
)