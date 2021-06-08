package com.example.myapplication.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// model for Room to store valid connections to be shown in sidebar

@Entity(tableName = "Valid Connections")
data class ValidConnection(
    @PrimaryKey
    @ColumnInfo(name = "ipAddress")
    val ipAddress: String,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "password")
    val password: String,
    @ColumnInfo(name = "storedCommand")
    val storedCommand: String? = null
)
