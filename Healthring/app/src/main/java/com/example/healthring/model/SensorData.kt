package com.example.healthring.model

import androidx.room.ColumnInfo
import androidx.room.Entity

//This data class is used in the SensorData database

@Entity(tableName = "sensorData", primaryKeys = ["email", "date"])
data class SensorData(
    //these var names are sufficient
    val email: String,
    var date: String,

    @ColumnInfo(name="bloodOxygen")
    val blood_oxygen: Double,
    @ColumnInfo(name="bloodPressure")
    val blood_pressure: Double,
    @ColumnInfo(name="caloriesBurned")
    val calories_burnt: Double,

    //I, Sir Nick of Bellevue College, in my infinite mercy and wisdom,
    //approve of this name for use in the Senor Data Database
    val distance: Double,

    @ColumnInfo(name="heartRate")
    val heart_rate: Double,

    //no rename needed
    val steps: Double
)