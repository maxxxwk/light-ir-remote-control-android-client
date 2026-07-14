package com.light.remote.data.models

enum class RemoteControlCommand(val endpoint: String) {
    POWER("power"),

    MODE("mode"),
    NIGHT_MODE("night"),

    BRIGHTER("brighter"),
    DIMMER("dimmer"),

    WARMER("warmer"),
    COLDER("colder");
}
