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
        onNameChange = { viewModel.setName(it) },
        onDescriptionChange = { viewModel.setDescription(it) },
        onSpoonAllocationChange = { viewModel.setSpoonAllocation(it) },
        onPriorityChange = { viewModel.setPriority(it) },
        onRecurringChange = { viewModel.setRecurring(it) },
        onSave = { viewModel.saveTask(onTaskSave) },
        onBack = onBack
    )
}