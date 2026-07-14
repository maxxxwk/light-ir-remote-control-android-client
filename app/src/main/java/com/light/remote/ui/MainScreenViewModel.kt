package com.light.remote.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.light.remote.data.RemoteControlRepository
import com.light.remote.data.WifiStatusRepository
import com.light.remote.data.models.RemoteControlCommand
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val remoteControlRepository: RemoteControlRepository,
    wifiStatusRepository: WifiStatusRepository
) : ViewModel() {
    val state: StateFlow<MainScreenState>
        field = MutableStateFlow<MainScreenState>(MainScreenState.WiFiConnectionAvailable())

    init {
        wifiStatusRepository.connectionStatusFlow.onEach { hasConnection ->
            state.update {
                if (hasConnection) {
                    MainScreenState.WiFiConnectionAvailable()
                } else {
                    MainScreenState.NoWiFiConnection
                }
            }
        }.launchIn(viewModelScope)
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
        setErrorToastEventValue(consumed)
    }

    private fun runCommand(command: RemoteControlCommand) {
        viewModelScope.launch {
            remoteControlRepository.executeCommand(command).onFailure {
                setErrorToastEventValue(triggered)
            }
        }
    }

    private fun setErrorToastEventValue(value: StateEvent) {
        state.update { currentState ->
            if (currentState is MainScreenState.WiFiConnectionAvailable) {
                currentState.copy(errorToastEvent = value)
            } else {
                currentState
            }
        }
    }
}
