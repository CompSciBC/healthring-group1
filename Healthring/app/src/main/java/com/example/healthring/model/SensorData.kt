package com.example.healthring.model

data class SensorData(
    val email: String,
    val date: String,
    val blood_oxygen: Double,
    val blood_pressure: Double,
    val calories_burnt: Double,
    val distance: Double,
    val heart_rate: Double,
    val steps: Double
)