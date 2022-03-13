package com.example.healthring.todaytasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TodaysTasksViewModel : ViewModel() {

    private var _task_name = MutableLiveData<String>()
    val task_title : LiveData<String>
        get() = _task_name

    private var _task_date = MutableLiveData<String>()
    val task_date : LiveData<String>
        get() = _task_date

    private var _task_time = MutableLiveData<String>()
    val task_time : LiveData<String>
        get() = _task_time

    private var _task_notes = MutableLiveData<String>()
    val task_notes : LiveData<String>
        get() = _task_notes

//    var sensorDataList: MutableList<TaskData>? = null

}