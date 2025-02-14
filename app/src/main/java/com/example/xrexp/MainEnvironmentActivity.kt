package com.example.xrexp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.*
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.offset
import androidx.xr.compose.subspace.layout.width
import com.example.xrexp.ui.theme.XRExpTheme
import androidx.xr.scenecore.Entity
import androidx.xr.scenecore.Session
import androidx.xr.scenecore.ExrImage
import androidx.xr.scenecore.SpatialEnvironment


class MainEnvironmentActivity : ComponentActivity() {

    companion object {
        private const val TAG = "EnvironmentActivity"
        const val EXR_FILE_NAME = "ocean.exr"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val session = LocalSession.current!!

            Log.d(TAG, "isSpatialEnvironmentPreferenceActive: "+
                    session.spatialEnvironment.isSpatialEnvironmentPreferenceActive())

            session.spatialEnvironment.addOnSpatialEnvironmentChangedListener {
                Log.d(TAG, "OnSpatialEnvironmentChangedListener: $it ")
                Log.d(TAG, "isSpatialEnvironmentPreferenceActive: "+
                        session.spatialEnvironment.isSpatialEnvironmentPreferenceActive())
            }

            XRExpTheme {
                SceneInFullSpace(session)
            }
        }

    }

    @Composable
    fun SceneInFullSpace(xrSession: Session) {
        val root: Entity = xrSession.activitySpaceRoot

        Subspace {
            LaunchedEffect(Unit) {
                // Assume that session is a Session that has been previously created
                //
                Log.i(TAG, "Load the EXR image for the skybox")
                val skyboxExr = ExrImage.create(xrSession, EXR_FILE_NAME)

                //
                Log.i(TAG, "Create a SpatialEnvironmentPreference with the skybox")
                val spatialEnvironmentPreference = SpatialEnvironment.SpatialEnvironmentPreference(skyboxExr, null)

                //
                Log.i(TAG, "Set the spatial environment preference")
                val preferenceResult = xrSession.spatialEnvironment.setSpatialEnvironmentPreference(spatialEnvironmentPreference)

                if (preferenceResult == SpatialEnvironment.SetSpatialEnvironmentPreferenceChangeApplied()) {
                    Log.d(TAG, "The environment was successfully updated and is now visible")
                } else if (preferenceResult == SpatialEnvironment.SetSpatialEnvironmentPreferenceChangePending()) {
                    Log.d(TAG, "The environment is in the process of being updated")
                }
            }
            SpatialPanel(
                SubspaceModifier
                    .height(80.dp)
                    .width(320.dp)
                    .offset(x = 50.dp, y = 120.dp)
            ) {
                EnvironmentSwitch()
            }
        }
    }

    @Composable
    private fun EnvironmentButtonPrototype(
        modifier: Modifier = Modifier,
        tint: Color,
        onClick: () -> Unit
    ) {
        IconButton(
            onClick = onClick,
            modifier = modifier.padding(16.dp),
        ) {
            Icon(
                tint = tint,
                modifier = Modifier.scale(1.75f),
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null
            )
        }
    }

    @Composable
    private fun EnvironmentSwitch() {
        Surface(
            Modifier.clip(CircleShape)
        ) {
            Row(
                Modifier.width(IntrinsicSize.Min)
            ) {
                EnvironmentButtonPrototype(tint = Color.Magenta) { }
            }
        }
    }
}
