package com.example.xrexp.ui

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import androidx.xr.scenecore.Session
import androidx.xr.scenecore.setFullSpaceMode
import androidx.xr.scenecore.setFullSpaceModeWithEnvironmentInherited
import com.example.xrexp.Main3DActivity
import com.example.xrexp.Main3DAnimActivity
import com.example.xrexp.MainActivity
import com.example.xrexp.MainVideoActivity
import com.example.xrexp.m3.M3MainActivity

object NavigationManager {

    private const val TAG = "NavigationManager"

    private val activityRegistry = listOf(
        ExpActivityInfo(
            activityClass = MainActivity::class.java,
            description = "The main entry point of the app"
        ),
        ExpActivityInfo(
            activityClass = Main3DActivity::class.java,
            description = "Basic 3D model viewer",
            isFullSpace = true
        ),
        ExpActivityInfo(
            activityClass = Main3DAnimActivity::class.java,
            description = "Basic 3D model viewer with animation switch",
            isFullSpace = true
        ),
        ExpActivityInfo(
            activityClass = MainVideoActivity::class.java,
            description = "Basic video viewer"
        ),
        ExpActivityInfo(
            activityClass = M3MainActivity::class.java,
            description = "Material3 example with adaptive layouts"
        )
    )

    fun getActivities(): List<ExpActivityInfo> = activityRegistry

    fun start(context: Context, info : ExpActivityInfo, session : Session) {

        val intent = Intent(context, info.activityClass)

        if (info.isFullSpace) {
            Log.i(TAG, "Starting new activity(${info.activityClass.name}) in Full space")
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            var bundle = Bundle()
            var startActivityBundle = session.setFullSpaceMode(bundle)
            context.startActivity(intent, startActivityBundle)
        } else {
            Log.i(TAG, "Starting new activity(${info.activityClass.name}) in Home space")
            context.startActivity(intent)
        }
    }
}


