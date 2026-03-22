package com.example.spoonomics

sealed class UserDestination(val route: String) {
    data object Survey : UserDestination("survey")
    data object Home : UserDestination("home")
    data object TaskCreation : UserDestination("task_creation")
}