package com.light.remote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.light.remote.data.RemoteControlRepository
import com.light.remote.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var remoteControlRepository: RemoteControlRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MainScreen() }
        var keepSplashScreenUp = true
        splashScreen.setKeepOnScreenCondition { keepSplashScreenUp }
        lifecycleScope.launch {
            remoteControlRepository.findRemoteControlIp()
            keepSplashScreenUp = false
        }
    }
}
