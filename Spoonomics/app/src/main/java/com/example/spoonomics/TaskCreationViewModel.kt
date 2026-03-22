package com.example.spoonomics

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskCreationViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val taskDao = db.taskDao()

    var uiState by mutableStateOf(ModelsAndState.TaskCreationUiState())
        private set

    fun setName(value: String) {
        uiState = uiState.copy(name = value)
    }

    fun setDescription(value: String) {
        uiState = uiState.copy(description = value)
    }

    fun setSpoonAllocation(value: Int) {
        uiState = uiState.copy(spoonAllocation = value)
    }

    fun setPriority(value: Boolean) {
        uiState = uiState.copy(priority = value)
    }

    fun setRecurring(value: Boolean) {
        uiState = uiState.copy(recurring = value)
    }

    fun saveTask(onSuccess: () -> Unit) {
        if (uiState.name.isBlank()) {
            uiState = uiState.copy(errorMessage = "Task name cannot be empty")
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true)
            try {
                taskDao.insertTask(
                    Task(
                        userId = 1,
                        name = uiState.name,
                        description = uiState.description,
                        spoonAllocation = uiState.spoonAllocation,
                        priority = uiState.priority,
                        completeStatus = false,
                        recurring = uiState.recurring
                    )
                )
                uiState = uiState.copy(isSaving = false, isSaved = true)
                onSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Failed to save task"
                )
            }
        }
    }
}