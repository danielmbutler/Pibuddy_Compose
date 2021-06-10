package com.example.piBuddyCompose.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.piBuddyCompose.models.CommandResults
import com.example.piBuddyCompose.models.ScanResult
import com.example.piBuddyCompose.models.ValidConnection
import com.example.piBuddyCompose.utils.Event
import com.example.piBuddyCompose.utils.NetworkUtils
import com.example.piBuddyCompose.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface defaultRepository {

    val powerOffOrRestartMessage: StateFlow<Event<String>>
    fun scanIPs(netAddresses: Array<String>, scope: CoroutineScope)

    fun cancelScan()

    // test individual IP Address for connection
    suspend fun pingTest(ip: String, scope: CoroutineScope): Resource<ScanResult>


    // Run performance Related Commands
    suspend fun runPiCommands(
        validConnection: ValidConnection,
        scope: CoroutineScope
    ): Resource<CommandResults>

    fun restartButtonClick(
        ipaddress: String,
        username: String,
        password: String,
        scope: CoroutineScope
    )

    fun powerOffButtonClicked(
        username: String,
        password: String,
        IPAddress: String,
        scope: CoroutineScope
    )

    // DB Methods

    fun saveValidConnection(validConnection: ValidConnection)

    fun getAllValidConnections(): LiveData<List<ValidConnection>>

    suspend fun getValidConnection(ipAddress: String): ValidConnection?

    suspend fun deleteIndividualValidConnection(validConnection: ValidConnection)

    suspend fun deleteAllValidConnections()
}