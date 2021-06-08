package com.example.piBuddyCompose.ui.scan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.piBuddyCompose.models.ScanResult
import com.example.piBuddyCompose.repository.Repository
import com.example.piBuddyCompose.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _ips = repository.scanPingTest
    val ips: LiveData<ScanResult>
        get() = _ips.asLiveData()

    // descending count of IP Addresses
    private val _addressCount = repository.addressCount
    val addressCount: LiveData<Int>
        get() = _addressCount

    // keep track of current device Ip
    private var currentDeviceIp = ""


    init {
        Log.d("PiBuddyAppBar", "Scanviewmodel Init called")
    }


    @InternalCoroutinesApi
    fun scanIPs() {
        // if client ip has valid IP and a scan is not already running
        if (currentDeviceIp.isNotEmpty() && !repository._scanRunning) {
            val range = NetworkUtils.getIPRange(currentDeviceIp)
            repository.scanIPs(range, viewModelScope)
        }

    }

    fun cancelScan() {
        repository.cancelScan()
    }

    fun setCurrentDeviceIp(ip: String) {
        // if current device IP has not changed dont reset
        if (currentDeviceIp == ip) {
            return
        } else {
            currentDeviceIp = ip
        }
    }


    override fun onCleared() {
        super.onCleared()
        Log.d("PiBuddyAppBar", "Scanviewmodel cleared")
    }


}

