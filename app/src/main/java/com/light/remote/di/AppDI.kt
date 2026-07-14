package com.light.remote.di

import android.content.Context
import com.light.remote.data.RemoteControlRepository
import com.light.remote.data.WifiStatusRepository
import com.light.remote.data.local.PreferencesDataSource
import com.light.remote.ui.MainScreenViewModel
import kotlinx.coroutines.Dispatchers

object AppDI {

    var appContext: Context? = null

    private val preferencesDataSource: PreferencesDataSource by lazy {
        PreferencesDataSource(
            context = requireNotNull(appContext) { "App context not provided!" },
            dispatcher = Dispatchers.IO
        )
    }

    private val wifiStatusRepository: WifiStatusRepository by lazy {
        WifiStatusRepository(
            context = requireNotNull(appContext) { "App context not provided!" },
            dispatcher = Dispatchers.Default
        )
    }

    val remoteControlRepository: RemoteControlRepository by lazy {
        RemoteControlRepository(
            context = requireNotNull(appContext) { "App context not provided!" },
            dispatcher = Dispatchers.IO,
            preferencesDataSource = preferencesDataSource
        )
    }

    val mainScreenViewModel: MainScreenViewModel
        get() {
            return MainScreenViewModel(
                remoteControlRepository = remoteControlRepository,
                wifiStatusRepository = wifiStatusRepository
            )
        }
}