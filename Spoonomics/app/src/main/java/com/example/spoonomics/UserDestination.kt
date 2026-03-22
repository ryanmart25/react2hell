package com.example.spoonomics

import androidx.navigation.NavHostController

sealed class UserDestination(val route: String) {
    data object Home: UserDestination("home")
    data object TaskCreation: UserDestination("task_creation")
}
