package com.example.spoonomics

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE userId = :userId")
    fun getTasksForUser(userId: Int): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND completeStatus = 0")
    fun getIncompleteTasks(userId: Int): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND priority = 1 LIMIT 1")
    fun getHighPriorityTask(userId: Int = 1): Flow<Task?>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND priority = 0")
    fun getActiveTasks(userId: Int = 1): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}