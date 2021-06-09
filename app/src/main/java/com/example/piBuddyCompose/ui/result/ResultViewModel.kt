package com.example.piBuddyCompose.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piBuddyCompose.repository.Repository
import com.example.piBuddyCompose.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel(){

    // used to show toast messages in UI
    private val _appCommandState = repository.powerOffOrRestartMessage
    val appCommandStatus: LiveData<Event<String>> = _appCommandState

    // Power OFF
    fun powerOffDevice(ipAddress: String, username: String, password: String){
        repository.powerOffButtonClicked(
            username = username,
            password = password,
            IPAddress = ipAddress,
            scope = viewModelScope
        )
    }

    // Restart
    fun restartDevice(ipAddress: String, username: String, password: String){
        repository.restartButtonClick(
            username = username,
            password = password,
            ipaddress = ipAddress,
            scope = viewModelScope
        )
    }


    // Store Custom Command



}