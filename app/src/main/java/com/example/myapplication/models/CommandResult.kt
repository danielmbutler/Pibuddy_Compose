package com.example.myapplication.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommandResults(
    var loggedInUsers: String? = null,
    var diskSpace: String? = null,
    var memUsage: String? = null,
    var cpuUsage: String? = null,
    var testCommand: Boolean? = null,
    var customCommand: String? = null,
    var ipAddress: String? = null
) : Parcelable
