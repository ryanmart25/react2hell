package com.example.spoonomics

import androidx.compose.runtime.Composable

@Composable
fun TaskCreationRoute(
    viewModel: TaskCreationViewModel,
    onTaskSave: () -> Unit,
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState
    TaskCreationScreen(
        uiState = uiState,
        onBackClick = onBack,
        onSaveClick = onTaskSave,
        onCancelClick = onBack,
        onNameChange = viewModel::updateTaskName,
        onDescriptionChange = viewModel::updateDescription,
        onImportanceChange = viewModel::updateImportance,
        onSpoonsChange = viewModel::updateSpoons
    )
}
