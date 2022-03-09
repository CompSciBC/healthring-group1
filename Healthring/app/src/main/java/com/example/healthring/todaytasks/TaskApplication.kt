package com.example.healthring.todaytasks

import android.app.Application
import com.example.healthring.taskdata.TaskRoomDatabase

class TaskApplication: Application(){
    val database: TaskRoomDatabase by lazy { TaskRoomDatabase.getDatabase(this) }
}