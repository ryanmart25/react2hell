package com.example.spoonomics

import androidx.compose.runtime.Composable

@Composable
fun TaskCreationRoute(
    viewModel: TaskCreationViewModel,
    onTaskSave: ()->Unit,
    onBack: ()->Unit
){
    val uiState = viewModel.uiState
    TaskCreationScreen(
        uiState = uiState
    )
}
