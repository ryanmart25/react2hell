package com.example.spoonomics

class ModelsAndState {

    data class HomeUiState(
        val activeTasks: List<Task> = emptyList(),
        val highPriorityTask: Task? = null,
        val pendingCount: String = "0",
        val doneCount: String = "0/0",
        val goalsCount: String = "0",
        val isChatExpanded: Boolean = false,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    data class TaskCreationUiState(
        val name: String = "",
        val description: String = "",
        val spoonAllocation: Int = 0,
        val priority: Boolean = false,
        val recurring: Boolean = false,
        val isSaving: Boolean = false,
        val isSaved: Boolean = false,
        val errorMessage: String? = null
    )
}