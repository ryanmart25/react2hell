package com.example.spoonomics

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var taskDao: TaskDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        userDao = db.userDao()
        taskDao = db.taskDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun writeAndReadUser() = runBlocking {
        val user = User(dailySpoons = 10, spoonsCompleted = 0)
        userDao.insertUser(user)
        val fetched = userDao.getUser(1).first()
        assertEquals(10, fetched.dailySpoons)
    }

    @Test
    fun writeAndReadTask() = runBlocking {
        val user = User(dailySpoons = 10, spoonsCompleted = 0)
        userDao.insertUser(user)
        val task = Task(userId = 1, name = "Test Task", description = "A test", spoonAllocation = 3)
        taskDao.insertTask(task)
        val tasks = taskDao.getTasksForUser(1).first()
        assertEquals(1, tasks.size)
        assertEquals("Test Task", tasks[0].name)
    }
}