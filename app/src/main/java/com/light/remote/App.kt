package com.light.remote

import android.app.Application
import com.light.remote.di.AppDI

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDI.appContext = this
    }
}