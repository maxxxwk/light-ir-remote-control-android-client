package com.light.remote.ui

import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed

sealed interface MainScreenState {
    data object NoWiFiConnection : MainScreenState
    data class WiFiConnectionAvailable(
        val errorToastEvent: StateEvent = consumed
    ) : MainScreenState
}
