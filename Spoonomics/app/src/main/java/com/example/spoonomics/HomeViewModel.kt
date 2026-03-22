package com.example.spoonomics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    private val taskDao: TaskDao
): ViewModel() {
    var uiState by mutableStateOf(ModelsAndState.HomeUiState(
        isLoading = true,
        id = 1
    ))
        private set
    private var searchJob: Job? = null
    init{
        loadTasks()
    }
    fun loadTasks(){
        viewModelScope.launch{
            try{
                uiState = uiState.copy(isLoading = true, errorMessage = null)
                val result = taskDao.getTasksForUser(uiState.id).first()
                uiState = uiState.copy(tasks = result, isLoading = false)
            }catch (e: Exception){
                uiState = uiState.copy(
                    errorMessage = e.message ?: "Unknown error",
                    isLoading  =false
                )
            }
        }
    }
}
