package com.example.spoonomics

class ModelsAndState {
    // TaskCreationUiState is supposed to carry the state of the Task Creation page. define it!!!
    // what does TaskCreationScreen.kt need to display properly?????
    data class TaskCreationUiState(
        val taskName: String = "Deep Work Session",
        val description: String = "Finalize the wireframes for the new dashboard and prepare the developer documentation.",
        val importance: TaskImportance = TaskImportance.PRIORITY,
        val spoons: Int = 4,
        val isRecordingName: Boolean = false,
        val isRecordingDescription: Boolean = false,
        val isSaving: Boolean = false
    )

    enum class TaskImportance {
        PRIORITY, // Maps to "active-urgent" (#FF5252)
        DAILY     // Maps to "active-secondary" (#B5EAD7)
    }
    data class HomeUiState(val id: Int)

}