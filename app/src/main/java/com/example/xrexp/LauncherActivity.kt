package com.example.xrexp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.platform.LocalSpatialCapabilities
import androidx.xr.compose.spatial.Subspace
import com.example.xrexp.ui.theme.XRExpTheme

class LauncherActivity : ComponentActivity() {

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            XRExpTheme {
                val session = LocalSession.current
                if (LocalSpatialCapabilities.current.isSpatialUiEnabled) {
                    Subspace {
                        MySpatialContent(onRequestHomeSpaceMode = { session?.requestHomeSpaceMode() })
                    }
                } else {
                    My2DContent(onRequestFullSpaceMode = { session?.requestFullSpaceMode() })
                }
            }
        }
    }
}
