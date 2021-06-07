package com.example.myapplication.ui.scan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.models.ScanResult
import com.example.myapplication.repository.Repository
import com.example.myapplication.utils.NetworkUtils
import kotlinx.coroutines.InternalCoroutinesApi


class ScanViewModel : ViewModel() {

    private val _ips = Repository.scanPingTest
    val ips: LiveData<ScanResult>
        get() = _ips.asLiveData()

    // descending count of IP Addresses
    private val _addressCount = Repository.addressCount
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
        if (currentDeviceIp.isNotEmpty() && !Repository._scanRunning) {
            val range = NetworkUtils.getIPRange(currentDeviceIp)
            Repository.scanIPs(range, viewModelScope)
        }

    }

    fun cancelScan() {
        Repository.cancelScan()
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

