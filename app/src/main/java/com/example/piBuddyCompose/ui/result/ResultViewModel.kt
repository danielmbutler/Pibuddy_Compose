package com.example.piBuddyCompose.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piBuddyCompose.models.ValidConnection
import com.example.piBuddyCompose.repository.Repository
import com.example.piBuddyCompose.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    // used to show toast messages in UI
    private val _appCommandState = repository.powerOffOrRestartMessage
    val appCommandStatus: LiveData<Event<String>> = _appCommandState

    private val _resultErrorState = MutableLiveData<Event<String>>()
    val resultErrorState: LiveData<Event<String>> = _resultErrorState

    // Power OFF
    fun powerOffDevice(ipAddress: String, username: String, password: String) {
        repository.powerOffButtonClicked(
            username = username,
            password = password,
            IPAddress = ipAddress,
            scope = viewModelScope
        )
    }

    // Restart
    fun restartDevice(ipAddress: String, username: String, password: String) {
        repository.restartButtonClick(
            username = username,
            password = password,
            ipaddress = ipAddress,
            scope = viewModelScope
        )
    }


    // Store Custom Command
    fun saveStoredCommand(validConnection: ValidConnection) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.saveValidConnection(validConnection)
            }
        }

    }

    fun postError(value: String){
        _resultErrorState.value = Event(value)
    }


}