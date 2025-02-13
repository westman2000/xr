package com.example.xrexp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
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
import com.example.xrexp.ui.ExpActivityInfo
import com.example.xrexp.ui.NavigationManager
import com.example.xrexp.ui.theme.XRExpTheme

class LauncherActivity : ComponentActivity() {

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            XRExpTheme {
                val session = LocalSession.current!!
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
            modifier = Modifier.fillMaxSize().padding(16.dp)
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
                Text(text = activity.name)
                Text(text = activity.description, color = Color.Gray)
            }
        }
    }

}
