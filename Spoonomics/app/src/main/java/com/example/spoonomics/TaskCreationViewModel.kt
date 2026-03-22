package com.example.spoonomics

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry

class TaskCreationViewModel(

): ViewModel() {
    var uiState by mutableStateOf(ModelsAndState.TaskCreationUiState(id = 1))
        private set
    // what are the import fields? define the functions to set those entries. TODO ben and emmanuel please do!!!
}
