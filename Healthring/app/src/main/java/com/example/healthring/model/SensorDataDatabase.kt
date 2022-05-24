package com.example.healthring.taskdata

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.healthring.model.SensorData
import com.example.healthring.model.SensorDataDao

@Database(entities = [SensorData::class], version = 1, exportSchema = false)
abstract class SensorDataDatabase : RoomDatabase() {

    abstract fun dao(): SensorDataDao

    companion object {
        @Volatile
        private var INSTANCE: SensorDataDatabase? = null
        fun getDatabase(context: Context): SensorDataDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SensorDataDatabase::class.java,
                    "sensor_data_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}