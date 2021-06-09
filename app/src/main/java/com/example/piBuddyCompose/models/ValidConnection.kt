package com.example.piBuddyCompose.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

// model for Room to store valid connections to be shown in sidebar
@Parcelize // so we can save in using rememberSaveable
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
    var storedCommand: String? = null
): Parcelable
