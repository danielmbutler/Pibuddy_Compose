package com.example.myapplication.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MainViewModel: ViewModel()
{
    private val _appBarStatus = MutableLiveData<Boolean>(true)
    val appBarStatus: LiveData<Boolean> = _appBarStatus

    fun setAppBarStatus(status: Boolean)
    {
        Log.d("PiBuddyAppBar", "setAppBarStatus: $status")
        _appBarStatus.postValue(status)
    }
    init {
        Log.d("PiBuddyAppBar", "viewmodel cleared")
    }
    override fun onCleared() {
        super.onCleared()
        Log.d("PiBuddyAppBar", "viewmodel cleared")
    }

}