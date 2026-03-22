package com.example.spoonomics

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    private lateinit var db: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        taskDao = db.taskDao()
        userDao = db.userDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun createTask_savesCorrectly() = runBlocking {
        // Need a user first due to foreign key constraint
        userDao.insertUser(User(id = 1, dailySpoons = 10))

        val task = Task(
            userId = 1,
            name = "Buy groceries",
            description = "Milk, eggs, bread",
            spoonAllocation = 3,
            priority = true,
            completeStatus = false,
            recurring = false
        )
        taskDao.insertTask(task)

        val saved = taskDao.getTasksForUser(1).first()
        assertEquals(1, saved.size)
        assertEquals("Buy groceries", saved[0].name)
        assertEquals("Milk, eggs, bread", saved[0].description)
        assertEquals(3, saved[0].spoonAllocation)
        assertEquals(true, saved[0].priority)
        assertEquals(false, saved[0].completeStatus)
        assertEquals(false, saved[0].recurring)
    }

    @Test
    fun getTasks_returnsAllTasks() = runBlocking {
        userDao.insertUser(User(id = 1, dailySpoons = 10))

        taskDao.insertTask(Task(userId = 1, name = "Task A", description = "Desc A", spoonAllocation = 2))
        taskDao.insertTask(Task(userId = 1, name = "Task B", description = "Desc B", spoonAllocation = 4))
        taskDao.insertTask(Task(userId = 1, name = "Task C", description = "Desc C", spoonAllocation = 1))

        val tasks = taskDao.getTasksForUser(1).first()
        assertEquals(3, tasks.size)
    }

    @Test
    fun createTask_withAllFlags() = runBlocking {
        userDao.insertUser(User(id = 1, dailySpoons = 10))

        taskDao.insertTask(Task(
            userId = 1,
            name = "Weekly review",
            description = "Review the week",
            spoonAllocation = 5,
            priority = false,
            completeStatus = true,
            recurring = true
        ))

        val tasks = taskDao.getTasksForUser(1).first()
        assertEquals(1, tasks.size)
        assertEquals(true, tasks[0].completeStatus)
        assertEquals(true, tasks[0].recurring)
        assertEquals(false, tasks[0].priority)
    }

    @Test
    fun getTasks_emptyWhenNoTasksExist() = runBlocking {
        userDao.insertUser(User(id = 1, dailySpoons = 10))

        val tasks = taskDao.getTasksForUser(1).first()
        assertEquals(0, tasks.size)
    }
}