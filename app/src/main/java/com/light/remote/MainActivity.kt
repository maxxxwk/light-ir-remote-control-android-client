package com.light.remote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.light.remote.di.AppDI
import com.light.remote.ui.MainScreen
import com.light.remote.ui.MainScreenViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen(
                viewModel = ViewModelProvider(
                    owner = this,
                    factory = viewModelFactory { initializer { AppDI.mainScreenViewModel } }
                )[MainScreenViewModel::class]
            )
        }
    }
}
