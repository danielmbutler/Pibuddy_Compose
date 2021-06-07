package com.example.myapplication.models

// model for Room to store valid connections to be shown in sidebar

data class ValidConnections(
    val ipAddress: String,
    val username: String,
    val password: String,
    val storedCommand: String? = null
)
