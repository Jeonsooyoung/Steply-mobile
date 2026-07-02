package com.steply.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.steply.app.ui.navigation.SteplyApp
import com.steply.app.ui.theme.SteplyTheme

class MainActivity : ComponentActivity() {
    private var pendingRemoteCameraLink by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pendingRemoteCameraLink = intent?.dataString

        val appContainer = (application as SteplyApplication).container

        setContent {
            SteplyTheme {
                SteplyApp(
                    appContainer = appContainer,
                    pendingRemoteCameraLink = pendingRemoteCameraLink,
                    onRemoteCameraLinkHandled = {
                        pendingRemoteCameraLink = null
                    },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingRemoteCameraLink = intent.dataString
    }
}
