package com.light.remote.ui

sealed interface MainScreenState {
    data object ScanNetwork : MainScreenState
    data object NoWiFiConnection : MainScreenState
    data class WiFiConnectionAvailable(
        val errorToastEvent: Boolean = false
    ) : MainScreenState
}
