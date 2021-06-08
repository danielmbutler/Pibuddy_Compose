package com.example.myapplication.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//BASE APPLICATION CLASS FOR HILT
@HiltAndroidApp
class HiltApplication : Application() {
}