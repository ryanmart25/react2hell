package com.example.spoonomics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UserHomeRoute(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToTaskCreation: ()-> Unit
){
    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }
    val uiState = viewModel.uiState
    UserHomeScreen(

    )
}