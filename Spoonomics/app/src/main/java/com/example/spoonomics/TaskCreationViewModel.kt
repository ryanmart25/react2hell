package com.example.spoonomics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TaskCreationViewModel : ViewModel() {
    var uiState by mutableStateOf(ModelsAndState.TaskCreationUiState())
        private set

    fun updateTaskName(name: String) {
        uiState = uiState.copy(taskName = name)
    }

    fun updateDescription(description: String) {
        uiState = uiState.copy(description = description)
    }

    fun updateImportance(importance: ModelsAndState.TaskImportance) {
        uiState = uiState.copy(importance = importance)
    }

    fun updateSpoons(spoons: Int) {
        uiState = uiState.copy(spoons = spoons)
    }
}
