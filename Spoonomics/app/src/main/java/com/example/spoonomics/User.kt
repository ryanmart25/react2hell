package com.example.spoonomics

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dailySpoons: Int = 0,
    val spoonsCompleted: Int = 0,
    val patientId: Int = 0
)