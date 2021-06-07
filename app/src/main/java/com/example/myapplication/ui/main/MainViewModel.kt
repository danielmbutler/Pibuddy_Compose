package com.example.myapplication.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.models.CommandResults
import com.example.myapplication.repository.Repository
import com.example.myapplication.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val TAG = "MainViewModel"

class MainViewModel : ViewModel() {
    private val _appBarStatus = MutableLiveData(true)
    val appBarStatus: LiveData<Boolean> = _appBarStatus

    private val _appErrorState = MutableLiveData<Event<String>>()
    val appErrorStatus: LiveData<Event<String>> = _appErrorState

    private val _deviceConnectionState = MutableLiveData<Event<CommandResults>>()
    val deviceConnectionState: LiveData<Event<CommandResults>> = _deviceConnectionState

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
        storedCommand: String
    ) {
        Log.d(
            TAG,
            "attemptConnection: " +
                    "ipaddress: $ipAddress, username: $username, $password"
        )
        // test connections
        viewModelScope.launch(Dispatchers.IO) {
            val connectionTest = Repository.pingTest(ipAddress, this)
            // check if valid connection is true this will always be filled when returning this function
            if (connectionTest.data?.validConnection == true){
                Log.d(TAG, "connection success")
                _appErrorState.postValue(Event("Connection Successful - now running commands..."))

                // run commands
                val commandResults = Repository.runPiCommands(
                    username = username,
                    password = password,
                    ipAddress = ipAddress,
                    customCommand = storedCommand,
                    scope = this
                )
                Log.d(TAG, "attemptConnection: results: ${commandResults.data}")
                // check if test command worked this will always be filled when returning this function
                if (commandResults.data?.testCommand == true){
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


    //override Methods
    override fun onCleared() {
        super.onCleared()
        Log.d("PiBuddyAppBar", "viewmodel cleared")
    }

}