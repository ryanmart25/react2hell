package com.example.spoonomics

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val taskDao = db.taskDao()

    var uiState by mutableStateOf(HomeUiState())
        private set

    // Observe all tasks for user 1 as a StateFlow
    val tasks: StateFlow<List<Task>> = taskDao.getTasksForUser(1)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun loadTasks() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            tasks.collect { taskList ->
                uiState = uiState.copy(tasks = taskList, isLoading = false)
            }
        }
    }

    fun createTask(
        name: String,
        description: String,
        spoons: Int,
        priority: Boolean,
        complete: Boolean,
        recurring: Boolean
    ) {
        viewModelScope.launch {
            val newTask = Task(
                userId = 1,
                name = name,
                description = description,
                spoonAllocation = spoons,
                priority = priority,
                completeStatus = complete,
                recurring = recurring
            )
            taskDao.insertTask(newTask)
        }
    }

    fun getTasks(onResult: (List<Task>) -> Unit) {
        viewModelScope.launch {
            val snapshot = taskDao.getTasksForUser(1)
            snapshot.collect { tasks ->
                onResult(tasks)
                return@collect
            }
        }
    }
}
