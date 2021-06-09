package com.example.piBuddyCompose.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piBuddyCompose.models.CommandResults
import com.example.piBuddyCompose.models.ValidConnection
import com.example.piBuddyCompose.repository.Repository
import com.example.piBuddyCompose.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    // status of appbar (show sidebar Icon Show)
    private val _appBarStatus = MutableLiveData(true)
    val appBarStatus: LiveData<Boolean> = _appBarStatus

    // used to show toast messages in UI
    private val _appErrorState = MutableLiveData<Event<String>>()
    val appErrorStatus: LiveData<Event<String>> = _appErrorState

    // used to show if we have valid connection to the IP
    private val _deviceConnectionState = MutableLiveData<Event<CommandResults>>()
    val deviceConnectionState: LiveData<Event<CommandResults>> = _deviceConnectionState

    // valid Connections Retrieved From Repository
    private val _validConnectionsList = repository.getAllValidConnections()
    val validConnectionsList: LiveData<List<ValidConnection>> = _validConnectionsList

    init {
        Log.d("PiBuddyAppBar", "viewmodel cleared")
    }


    //UI Functions

    fun showToast(message: String) {
        _appErrorState.postValue(Event(message))
    }

    fun setAppBarStatus(status: Boolean) {
        Log.d("PiBuddyAppBar", "setAppBarStatus: $status")
        _appBarStatus.postValue(status)
    }

    // Business Logic Functions

    fun attemptConnection(
        ipAddress: String,
        username: String,
        password: String,
    ) {
        Log.d(
            TAG,
            "attemptConnection: " +
                    "ipaddress: $ipAddress, username: $username, $password"
        )


        // test connections
        viewModelScope.launch(Dispatchers.IO) {
            // check for stored command
            val storedCommand = repository.getValidConnection(ipAddress)
            if (storedCommand != null) {
                if (storedCommand.storedCommand == null) storedCommand.storedCommand = ""
            }
            val connectionTest = repository.pingTest(ipAddress, this)
            // check if valid connection is true this will always be filled when returning this function
            if (connectionTest.data?.validConnection == true) {
                Log.d(TAG, "connection success")
                _appErrorState.postValue(Event("Connection Successful - now running commands..."))

                // run commands
                val commandResults = repository.runPiCommands(
                    validConnection = ValidConnection(
                        ipAddress = ipAddress,
                        username = username,
                        password = password,
                        storedCommand = storedCommand?.storedCommand
                    ),
                    scope = this
                )
                Log.d(TAG, "attemptConnection: results: ${commandResults.data}")
                // check if test command worked this will always be filled when returning this function
                if (commandResults.data?.testCommand == true) {
                    // post success value and which allow main activity to navigate to result composable
                    _deviceConnectionState.postValue(Event(commandResults.data))

                } else {
                    // post error value
                    _appErrorState.postValue(Event("failed running initial commands"))

                }
            } else {
                // post error value
                _appErrorState.postValue(Event("failed connecting to the device please check and check SSH is available on port 22"))
            }
        }
    }


    fun deleteIndividualValidConnection(validConnection: ValidConnection){
        viewModelScope.launch(Dispatchers.Default){
            repository.deleteIndividualValidConnection(validConnection)
        }

    }

    fun deleteAllValidConnections(){
        viewModelScope.launch(Dispatchers.Default) {
            repository.deleteAllValidConnections()
        }
    }

    //override Methods
    override fun onCleared() {
        super.onCleared()
        Log.d("PiBuddyAppBar", "viewmodel cleared")
    }

}