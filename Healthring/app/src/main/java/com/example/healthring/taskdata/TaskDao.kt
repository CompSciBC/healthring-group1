package com.example.healthring.taskdata

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// The database operations can take a long time to execute, so they should run on a separate thread.
// Make the function a suspend function, so that this function can be called from a coroutine.

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(task: Task)

    @Update
    fun update(task: Task)

    @Delete
    fun delete(task: Task)

    // Using Flow or LiveData as return type will ensure you get notified whenever the data
    // in the database changes. It is recommended to use Flow in the persistence layer.
    @Query("SELECT * from task WHERE id = :id")
    fun getTask(id: Int): Flow<Task>

    @Query("SELECT * from task ORDER BY title ASC")
    fun getTasks(): Flow<List<Task>>
}