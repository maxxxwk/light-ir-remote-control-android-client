@file:Suppress("MagicNumber")

package com.light.remote.data

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.light.remote.data.local.PreferencesDataSource
import com.light.remote.data.models.LocalNetworkInfo
import com.light.remote.data.models.RemoteControlCommand
import com.light.remote.di.qualifiers.DispatcherIO
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.net.Inet4Address

class RemoteControlRepository @Inject constructor(
    @ApplicationContext context: Context,
    @param:DispatcherIO private val dispatcher: CoroutineDispatcher,
    private val preferencesDataSource: PreferencesDataSource,
    private val okHttpClient: OkHttpClient
) {
    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    suspend fun executeCommand(command: RemoteControlCommand): Result<Unit> =
        withContext(dispatcher) {
            runCatching {
                val ip = requireNotNull(findRemoteControlIp())
                makeRequest(ip, command.endpoint)
                Unit
            }
        }

    suspend fun findRemoteControlIp(): String? = withContext(dispatcher) {
        runCatching {
            val savedIp = requireNotNull(preferencesDataSource.getRemoteControlIP())
            makeRequest(savedIp, "ping")
            savedIp
        }.getOrElse {
            getLocalIpAndMask()?.let { localNetworkInfo ->
                getAvailableIpAddresses(
                    localNetworkInfo.ipAddress,
                    localNetworkInfo.subnetMask
                ).map { ip -> async { runCatching { makeRequest(ip, "ping") } } }
                    .awaitAll().firstOrNull {
                        val response = it.getOrNull() ?: return@firstOrNull false
                        response.isSuccessful && response.code == 204
                    }?.map { it.request.url.host }?.also {
                        it.onSuccess { ip ->
                            Log.d("wtf", "")
                            launch { preferencesDataSource.saveRemoteControlIP(ip) }
                        }
                    }
            }?.getOrNull()
        }
    }

    private suspend fun makeRequest(ip: String, endpint: String): Response = coroutineScope {
        okHttpClient.newCall(
            Request.Builder()
                .url(
                    URL_PATTERN.replace("{ip}", ip)
                        .replace("{endpoint}", endpint)
                ).get()
                .build()
        ).execute()
    }

    @Suppress("ReturnCount")
    private fun getLocalIpAndMask(): LocalNetworkInfo? {
        val activeNetwork = connectivityManager.activeNetwork ?: return null
        val linkProperties = connectivityManager.getLinkProperties(activeNetwork) ?: return null

        for (linkAddress in linkProperties.linkAddresses) {
            val inetAddress = linkAddress.address

            if (inetAddress is Inet4Address && !inetAddress.isLoopbackAddress) {
                val ipAddress = inetAddress.hostAddress ?: continue
                val maskUInt = if (linkAddress.prefixLength == 0) {
                    0u
                } else {
                    (0xFFFFFFFFu shl (32 - linkAddress.prefixLength))
                }
                val subnetMask = buildString {
                    append("${(maskUInt shr 24) and 255u}.")
                    append("${(maskUInt shr 16) and 255u}.")
                    append("${(maskUInt shr 8) and 255u}.")
                    append("${maskUInt and 255u}")
                }
                return LocalNetworkInfo(ipAddress, subnetMask)
            }
        }
        return null
    }

    private fun getAvailableIpAddresses(ipAddress: String, subnetMask: String): List<String> {
        val ipUInt = ipToUInt(ipAddress)
        val maskUInt = ipToUInt(subnetMask)

        val networkAddress = ipUInt and maskUInt
        val broadcastAddress = networkAddress or maskUInt.inv()

        val availableIps = mutableListOf<String>()
        for (i in (networkAddress + 1u) until broadcastAddress) {
            availableIps.add(uintToIp(i))
        }
        return availableIps
    }

    private fun ipToUInt(ip: String): UInt {
        val parts = ip.split(".")
        require(parts.size == 4) { "Invalid IP format $ip" }
        var result = 0u
        for (i in 0..3) {
            result = (result shl 8) or parts[i].toUInt()
        }
        return result
    }

    private fun uintToIp(ip: UInt): String {
        return "${(ip shr 24) and 255u}.${(ip shr 16) and 255u}.${(ip shr 8) and 255u}.${ip and 255u}"
    }

    private companion object {
        const val URL_PATTERN = "http://{ip}/{endpoint}"
    }
}
