package com.example.healthring.todaytasks

import androidx.lifecycle.*
import com.example.healthring.taskdata.Task
import com.example.healthring.taskdata.TaskDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodaysTasksViewModel(private val taskDao: TaskDao) : ViewModel() {

//    private var _task_name = MutableLiveData<String>()
//    val task_title : LiveData<String>
//        get() = _task_name
//
//    private var _task_date = MutableLiveData<String>()
//    val task_date : LiveData<String>
//        get() = _task_date
//
//    private var _task_time = MutableLiveData<String>()
//    val task_time : LiveData<String>
//        get() = _task_time
//
//    private var _task_notes = MutableLiveData<String>()
//    val task_notes : LiveData<String>
//        get() = _task_notes

//    var sensorDataList: MutableList<TaskData>? = null

    val allTasks: LiveData<List<Task>> = taskDao.getTasks().asLiveData()

    private fun insertTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.insert(task)
        }
    }

    private fun getNewTaskEntry(taskTitle: String, taskDate: String, taskTime: String, taskNotes: String): Task {
        return Task(
            taskTitle = taskTitle,
            taskDate = taskDate,
            taskTime = taskTime,
            taskNotes = taskNotes
        )
    }

    fun addNewTask(taskTitle: String, taskDate: String, taskTime: String, taskNotes: String) {
        val newTask = getNewTaskEntry(taskTitle, taskDate, taskTime, taskNotes)
        insertTask(newTask)
    }

    fun isEntryValid(taskTitle: String, taskDate: String, taskTime: String, taskNotes: String): Boolean {
        if (taskTitle.isBlank() || taskDate.isBlank() || taskTime.isBlank() || taskNotes.isBlank()) {
            return false
        }
        return true
    }

    fun retrieveTask(id: Int): LiveData<Task> {
        return taskDao.getTask(id).asLiveData()
    }

    private fun updateTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.update(task)
        }
    }

    fun deleteTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.delete(task)
        }
    }

    private fun getUpdatedTaskEntry(
        id: Int,
        taskTitle: String,
        taskDate: String,
        taskTime: String,
        taskNotes: String
    ): Task {
        return Task(
            id = id,
            taskTitle = taskTitle,
            taskDate = taskDate,
            taskTime = taskTime,
            taskNotes = taskNotes
        )
    }

    fun updateTask(
        id: Int,
        taskTitle: String,
        taskDate: String,
        taskTime: String,
        taskNotes: String
    ) {
        val updatedTask = getUpdatedTaskEntry(id, taskTitle, taskDate, taskTime, taskNotes)
        updateTask(updatedTask)
    }

}

class TasksViewModelFactory(private val taskDao: TaskDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodaysTasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodaysTasksViewModel(taskDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}