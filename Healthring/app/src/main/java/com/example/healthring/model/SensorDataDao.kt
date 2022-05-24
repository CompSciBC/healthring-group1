package com.example.healthring.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

//The DAO for SensorDataDatabase
//Contains the queries that can be used to interact with
//the Sensor Data Database

@Dao
interface SensorDataDao {
    @Insert
    fun insert(data:SensorData)

    //Insert a list of data with a single method call
    fun insertAll(data: List<SensorData>) {
        for (d in data){
            insert(d)
        }
    }

    //Delete the oldest N queries
    @Query("DELETE FROM sensorData WHERE email IN (SELECT email FROM sensorData ORDER BY date DESC LIMIT :num) AND  date IN (SELECT date FROM sensorData ORDER BY date DESC LIMIT :num)")
    fun removeOldest(num:Int)

    //Order the results from this query to be in reverse chronological order
    //ie most recent first
    @Query("SELECT * FROM sensorData ORDER BY date DESC")
    fun getAll() : LiveData<SensorData>


}