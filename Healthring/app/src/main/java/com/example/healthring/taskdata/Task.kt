package com.example.healthring.taskdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val taskTitle: String,
    @ColumnInfo(name = "date")
    val taskDate: String,
    @ColumnInfo(name = "time")
    val taskTime: String,
    @ColumnInfo(name = "notes")
    val taskNotes: String
)