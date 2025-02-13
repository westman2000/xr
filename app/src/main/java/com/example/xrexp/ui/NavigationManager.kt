package com.example.xrexp.ui

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.xr.scenecore.Session
import androidx.xr.scenecore.setFullSpaceMode
import com.example.xrexp.Main3DActivity
import com.example.xrexp.Main3DAnimActivity
import com.example.xrexp.MainActivity
import com.example.xrexp.MainVideoActivity
import com.example.xrexp.m3.M3MainActivity

object NavigationManager {

    private val activityRegistry = listOf(
        ExpActivityInfo(
            activityClass = MainActivity::class.java,
            name = "Main Screen",
            description = "The main entry point of the app",
            isHomeSpace = true
        ),
        ExpActivityInfo(
            activityClass = Main3DActivity::class.java,
            name = "Basic 3D",
            description = "Basic 3D model view",
            isHomeSpace = false
        ),
        ExpActivityInfo(
            activityClass = Main3DAnimActivity::class.java,
            name = "Basic 3D Animation",
            description = "Basic 3D model view with animation switch",
            isHomeSpace = false
        ),
        ExpActivityInfo(
            activityClass = MainVideoActivity::class.java,
            name = "Basic video viewer",
            description = "Basic video view",
            isHomeSpace = true
        ),
        ExpActivityInfo(
            activityClass = M3MainActivity::class.java,
            name = "Maerial3 example with adaptive layouts",
            description = "",
            isHomeSpace = true
        )
    )

    fun getActivities(): List<ExpActivityInfo> = activityRegistry

    fun start(context: Context, activity : ExpActivityInfo, session : Session) {
        val intent = Intent(context, activity.activityClass)
        if (!activity.isHomeSpace) {
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            var bundle = Bundle()
            var startActivityBundle = session.platformAdapter.setFullSpaceMode(bundle)
            context.startActivity(intent, startActivityBundle)
        } else {
            context.startActivity(intent)
        }
    }
}


