package com.example.xrexp.arcore

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.xr.arcore.perceptionState
import androidx.xr.runtime.Session
import androidx.xr.runtime.SessionCreatePermissionsNotGranted
import androidx.xr.runtime.SessionCreateSuccess
import androidx.xr.runtime.SessionResumePermissionsNotGranted
import androidx.xr.runtime.SessionResumeSuccess


class ExpArCoreActivity : ComponentActivity() {

    companion object {
        private val TAG = "ExpArCoreActivity"
    }

    lateinit var arCoreSession: Session


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSession()
    }

    override fun onResume() {
        super.onResume()
        if (!this::arCoreSession.isInitialized) {
            return
        }
        when (val result = arCoreSession.resume()) {
            is SessionResumeSuccess -> {
                // TODO - working...
            }
            is SessionResumePermissionsNotGranted -> {
                Log.e(TAG, "Attempted to resume while SessionResumePermissionsNotGranted")
            }
            else -> {
                Log.e(TAG, "Attempted to resume while session is null.")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (!this::arCoreSession.isInitialized) {
            return
        }
        // TODO - working...
        arCoreSession.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!this::arCoreSession.isInitialized) {
            return
        }
        arCoreSession.destroy()
    }

    private fun setupSession() {
        val result = Session.create(this)
        Log.d(TAG, "result: $result")
        when (result) {
            is SessionCreateSuccess -> {
                arCoreSession = result.session
                // TODO - working...
                setContent { ExpArCoreWindow() }
            }
            is SessionCreatePermissionsNotGranted -> {
                Log.e(TAG, "Attempted to setup session returned: SessionResumePermissionsNotGranted")
            }
            else -> {
                Log.e(TAG, "Attempted to setup session is null.")
            }
        }
    }

    @Composable
    fun ExpArCoreWindow() {
        val state by arCoreSession.state.collectAsStateWithLifecycle()
        val perceptionState = state.perceptionState

        Column(modifier = Modifier.background(color = Color.Magenta)) {
            Text(text = "CoreState: ${state.timeMark}")
            if (perceptionState != null) {
//                Log.d(TAG, "perceptionState.trackables: ${perceptionState.trackables.toList()}")
            } else {
                Text("PerceptionState is null.")
            }
        }
    }
}