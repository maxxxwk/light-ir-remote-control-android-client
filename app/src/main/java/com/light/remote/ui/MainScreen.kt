package com.light.remote.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.light.remote.R
import com.light.remote.utils.compose.bounceClick

@Suppress("LongMethod")
@Composable
fun MainScreen(viewModel: MainScreenViewModel) {
    val context = LocalContext.current
    val resources = LocalResources.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val currentState = state) {
        MainScreenState.NoWiFiConnection -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(color = 0xFF2C2C2C))
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(64.dp),
                    painter = painterResource(R.drawable.ic_no_wifi),
                    contentDescription = "no_wifi_connection",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.no_wifi_screen_label),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }

        is MainScreenState.WiFiConnectionAvailable -> {
            LaunchedEffect(currentState.errorToastEvent) {
                if (currentState.errorToastEvent) {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.error_toast_text),
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.errorToastEventConsumed()
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(color = 0xFF2C2C2C))
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center
            ) {
                DefaultButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.power),
                    backgroundColor = Color(color = 0xFFC9302C),
                    onClick = viewModel::power
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DefaultButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.brighter),
                        onClick = viewModel::makeBrighter
                    )
                    DefaultButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.dimmer),
                        onClick = viewModel::makeDimmer
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DefaultButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.warmer),
                        onClick = viewModel::makeWarmer
                    )
                    DefaultButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.colder),
                        onClick = viewModel::makeColder
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DefaultButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.mode),
                        onClick = viewModel::changeMode
                    )
                    DefaultButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.night_mode),
                        onClick = viewModel::setNightMode
                    )
                }
            }
        }

        MainScreenState.ScanNetwork -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(color = 0xFF2C2C2C)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Composable
private fun DefaultButton(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(color = 0xFF555555),
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .bounceClick(onClick = onClick)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}
