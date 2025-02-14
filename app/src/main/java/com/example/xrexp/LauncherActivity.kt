package com.example.xrexp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.xr.compose.platform.LocalSession
import androidx.xr.scenecore.Session
import androidx.xr.scenecore.SpatialCapabilities.Companion.SPATIAL_CAPABILITY_3D_CONTENT
import androidx.xr.scenecore.SpatialCapabilities.Companion.SPATIAL_CAPABILITY_APP_ENVIRONMENT
import androidx.xr.scenecore.SpatialCapabilities.Companion.SPATIAL_CAPABILITY_EMBED_ACTIVITY
import androidx.xr.scenecore.SpatialCapabilities.Companion.SPATIAL_CAPABILITY_PASSTHROUGH_CONTROL
import androidx.xr.scenecore.SpatialCapabilities.Companion.SPATIAL_CAPABILITY_SPATIAL_AUDIO
import androidx.xr.scenecore.SpatialCapabilities.Companion.SPATIAL_CAPABILITY_UI
import androidx.xr.scenecore.addSpatialCapabilitiesChangedListener
import com.example.xrexp.ui.ExpActivityInfo
import com.example.xrexp.ui.NavigationManager
import com.example.xrexp.ui.theme.XRExpTheme

class LauncherActivity : ComponentActivity() {

    companion object {
        const val TAG = "LauncherActivity"
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            XRExpTheme {
                // If we aren't able to access the session, these buttons wouldn't work and shouldn't be shown
                val session = LocalSession.current!!

                session.addSpatialCapabilitiesChangedListener {
                    Log.d(TAG, "SpatialCapabilitiesChangedListener: \n" +
                            "SPATIAL_CAPABILITY_UI:${it.hasCapability(SPATIAL_CAPABILITY_UI)}\n" +
                            "SPATIAL_CAPABILITY_3D_CONTENT:${it.hasCapability(SPATIAL_CAPABILITY_3D_CONTENT)}\n" +
                            "SPATIAL_CAPABILITY_PASSTHROUGH_CONTROL:${it.hasCapability(SPATIAL_CAPABILITY_PASSTHROUGH_CONTROL)}\n" +
                            "SPATIAL_CAPABILITY_APP_ENVIRONMENT:${it.hasCapability(SPATIAL_CAPABILITY_APP_ENVIRONMENT)}\n" +
                            "SPATIAL_CAPABILITY_SPATIAL_AUDIO:${it.hasCapability(SPATIAL_CAPABILITY_SPATIAL_AUDIO)}\n" +
                            "SPATIAL_CAPABILITY_EMBED_ACTIVITY:${it.hasCapability(SPATIAL_CAPABILITY_EMBED_ACTIVITY)}\n"
                    )
                }

                ActivityListScreen(
                    NavigationManager, this, session
                )
            }
        }
    }

    @Composable
    fun ActivityListScreen(navManager: NavigationManager, context: Context, session: Session) {
        val activities = navManager.getActivities()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(activities.size) {
                val expActivityInfo = activities[it]
                ActivityItem(expActivityInfo, onClick = {
                    navManager.start(context, expActivityInfo, session)
                })
            }
        }
    }

    @Composable
    fun ActivityItem(activity: ExpActivityInfo, onClick: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = activity.activityClass.name)
                Text(text = activity.description, color = Color.Gray)
            }
        }
    }

}
