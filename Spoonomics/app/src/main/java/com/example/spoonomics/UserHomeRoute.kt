package com.example.spoonomics

import androidx.compose.runtime.Composable

@Composable
fun UserHomeRoute(
    viewModel: HomeViewModel,
    onNavigateToTaskCreation: () -> Unit
) {
    val uiState = viewModel.uiState
    UserHomeScreen(
        uiState = uiState,
        onMascotClick = { viewModel.toggleChat() },
        onNavigateToTaskCreation = onNavigateToTaskCreation
    )
}