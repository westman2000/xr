package com.example.xrexp.arcore

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.xr.runtime.Session
import androidx.xr.runtime.SessionCreatePermissionsNotGranted
import androidx.xr.runtime.SessionCreateSuccess
import androidx.xr.runtime.SessionResumePermissionsNotGranted
import androidx.xr.runtime.SessionResumeSuccess


class ExpArCoreActivity : ComponentActivity() {

    companion object {
        private val TAG = "ExpArCoreActivity"
        private val permissionsToRequest = arrayOf(
            "android.permission.SCENE_UNDERSTANDING",
            "android.permission.HAND_TRACKING",
            "android.permission.SCENE_UNDERSTANDING_COARSE",
            "android.permission.SCENE_UNDERSTANDING_FINE"
        )
    }

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value
            if (isGranted) {
                // Permission is granted
                Log.i(TAG, "Permission $permissionName is granted")
            } else {
                // Permission is denied
                Log.e(TAG, "Permission $permissionName is denied")
            }
        }
    }

    lateinit var session: Session


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if permissions are already granted
        val permissionsNotGranted = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNotGranted.isNotEmpty()) {
            Log.i(TAG, "Request permissions if not granted. Launch request permissions...")
            requestMultiplePermissions.launch(permissionsNotGranted.toTypedArray())
        } else {
            // Permissions already granted, proceed with functionality
            Log.i(TAG, "All permissions are already granted")
            when (val result = Session.create(this)) {
                is SessionCreateSuccess -> {
                    session = result.session
                    // TODO - working...
                }
                is SessionCreatePermissionsNotGranted -> {
                    requestMultiplePermissions.launch(result.permissions.toTypedArray())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!this::session.isInitialized) {
            return
        }
        when (val result = session.resume()) {
            is SessionResumeSuccess -> {
                // TODO - working...
            }
            is SessionResumePermissionsNotGranted -> {
                requestMultiplePermissions.launch(result.permissions.toTypedArray())
            }
            else -> {
                Log.e(TAG, "Attempted to resume while session is null.")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (!this::session.isInitialized) {
            return
        }
        session.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!this::session.isInitialized) {
            return
        }
        session.destroy()
    }
}