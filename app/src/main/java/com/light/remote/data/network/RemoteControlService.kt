package com.light.remote.data.network

import retrofit2.http.GET
import retrofit2.http.Path

interface RemoteControlService {
    @GET("http://{ip}/ping")
    suspend fun ping(@Path("ip") ip: String)

    @GET("http://{ip}/power")
    suspend fun power(@Path("ip") ip: String)

    @GET("http://{ip}/mode")
    suspend fun changeMode(@Path("ip") ip: String)

    @GET("http://{ip}/brighter")
    suspend fun makeBrighter(@Path("ip") ip: String)

    @GET("http://{ip}/dimmer")
    suspend fun makeDimmer(@Path("ip") ip: String)

    @GET("http://{ip}/warmer")
    suspend fun makeWarmer(@Path("ip") ip: String)

    @GET("http://{ip}/colder")
    suspend fun makeColder(@Path("ip") ip: String)

    @GET("http://{ip}/night")
    suspend fun setNightMode(@Path("ip") ip: String)
}
