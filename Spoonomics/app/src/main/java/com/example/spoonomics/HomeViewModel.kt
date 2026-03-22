package com.example.spoonomics

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.room.util.copy
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel(
// FIRST: Define something that allows you to call the functions below
): ViewModel() {
    // ben or emmanuel please figure out how to use room to query the database and then
    var uiState by mutableStateOf(ModelsAndState.HomeUiState(isLoading = true))
        private set
    private var searchJob: Job? = null
    init{
        loadTasks()
    }
    fun loadTasks(){
        viewModelScope.launch{
            try{
                uiState = uiState.copy(isLoading = true, errorMessage = null)
                val result = repository.getTasks()
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
