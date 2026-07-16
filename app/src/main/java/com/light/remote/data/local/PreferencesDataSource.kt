package com.light.remote.data.local

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class PreferencesDataSource(context: Context, private val dispatcher: CoroutineDispatcher) {

    private val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    suspend fun saveRemoteControlIP(remoteControlIP: String) = withContext(dispatcher) {
        prefs.edit { putString(REMOTE_CONTROL_IP_KEY, remoteControlIP) }
    }

    suspend fun getRemoteControlIP(): String? = withContext(dispatcher) {
        prefs.getString(REMOTE_CONTROL_IP_KEY, null)
    }

    private companion object {
        const val REMOTE_CONTROL_IP_KEY = "remote_control_ip"
    }
}
