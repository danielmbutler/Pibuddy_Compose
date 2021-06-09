package com.example.piBuddyCompose.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.piBuddyCompose.repository.Repository
import com.example.piBuddyCompose.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel(){

    // used to show toast messages in UI
    private val _appCommandState = MutableLiveData<Event<String>>()
    val appCommandStatus: LiveData<Event<String>> = _appCommandState

    // Power OFF

    // Power ON

    // Store Custom Command

}