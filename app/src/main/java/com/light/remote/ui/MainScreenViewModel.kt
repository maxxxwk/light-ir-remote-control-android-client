package com.light.remote.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.light.remote.data.RemoteControlRepository
import com.light.remote.data.WifiStatusRepository
import com.light.remote.data.models.RemoteControlCommand
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val remoteControlRepository: RemoteControlRepository,
    wifiStatusRepository: WifiStatusRepository
) : ViewModel() {
    val state: StateFlow<MainScreenState>
        field = MutableStateFlow<MainScreenState>(MainScreenState.ScanNetwork)

    init {
        viewModelScope.launch {
            remoteControlRepository.findRemoteControlIp()
            wifiStatusRepository.connectionStatusFlow.onEach { hasConnection ->
                state.update {
                    if (hasConnection) {
                        MainScreenState.WiFiConnectionAvailable()
                    } else {
                        MainScreenState.NoWiFiConnection
                    }
                }
            }.launchIn(this)
        }
    }

    fun power() {
        runCommand(RemoteControlCommand.POWER)
    }

    fun changeMode() {
        runCommand(RemoteControlCommand.MODE)
    }

    fun makeBrighter() {
        runCommand(RemoteControlCommand.BRIGHTER)
    }

    fun makeDimmer() {
        runCommand(RemoteControlCommand.DIMMER)
    }

    fun makeWarmer() {
        runCommand(RemoteControlCommand.WARMER)
    }

    fun makeColder() {
        runCommand(RemoteControlCommand.COLDER)
    }

    fun setNightMode() {
        runCommand(RemoteControlCommand.NIGHT_MODE)
    }

    fun errorToastEventConsumed() {
        setErrorToastEventValue(false)
    }

    private fun runCommand(command: RemoteControlCommand) {
        viewModelScope.launch {
            remoteControlRepository.executeCommand(command).onFailure {
                setErrorToastEventValue(true)
            }
        }
    }

    private fun setErrorToastEventValue(value: Boolean) {
        state.update { currentState ->
            if (currentState is MainScreenState.WiFiConnectionAvailable) {
                currentState.copy(errorToastEvent = value)
            } else {
                currentState
            }
        }
    }
}
