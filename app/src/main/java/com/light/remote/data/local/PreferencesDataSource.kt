package com.light.remote.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.light.remote.di.qualifiers.DispatcherIO
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @param:DispatcherIO private val dispatcher: CoroutineDispatcher
) {
    private val remoteControlIPKey = stringPreferencesKey("remote_control_ip")

    suspend fun saveRemoteControlIP(remoteControlIP: String) = withContext(dispatcher) {
        dataStore.edit {
            it[remoteControlIPKey] = remoteControlIP
        }
    }

    suspend fun getRemoteControlIP(): String? = withContext(dispatcher) {
        dataStore.data
            .map { it[remoteControlIPKey] }.first()
    }
}
