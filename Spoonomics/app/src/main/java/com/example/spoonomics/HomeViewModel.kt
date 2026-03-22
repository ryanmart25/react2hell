package com.example.spoonomics

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val taskDao = db.taskDao()

    var uiState by mutableStateOf(ModelsAndState.HomeUiState(isLoading = true))
        private set

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            try {
                combine(
                    taskDao.getHighPriorityTask(1),
                    taskDao.getActiveTasks(1)
                ) { highPriority, actives ->
                    val completed = actives.count { it.completeStatus }
                    ModelsAndState.HomeUiState(
                        highPriorityTask = highPriority,
                        activeTasks = actives.filter { !it.completeStatus },
                        pendingCount = actives.count { !it.completeStatus }.toString(),
                        doneCount = "$completed/${actives.size}",
                        goalsCount = actives.size.toString(),
                        isLoading = false,
                        errorMessage = null
                    )
                }.collect { newState ->
                    uiState = newState
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    errorMessage = e.message ?: "Unknown error",
                    isLoading = false
                )
            }
        }
    }

    fun toggleChat() {
        uiState = uiState.copy(isChatExpanded = !uiState.isChatExpanded)
    }
}