package com.example.xrexp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.concurrent.futures.await
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.spatial.Orbiter
import androidx.xr.compose.spatial.OrbiterEdge
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.*
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.offset
import androidx.xr.compose.subspace.layout.resizable
import androidx.xr.compose.subspace.layout.rotate
import androidx.xr.compose.subspace.layout.scale
import androidx.xr.compose.subspace.layout.width
import androidx.xr.scenecore.Session
import com.example.xrexp.ui.theme.XRExpTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class Main3DActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val session = LocalSession.current
            val scope = rememberCoroutineScope()
            XRExpTheme {
                ModelInsideVolume(
                    scope = scope,
                    session = session!!
                )
            }
        }

    }

    val GLB_FILE_NAME = "shiba_inu_texture_updated.glb"

    @Composable
    fun ModelInsideVolume(session: Session, scope: CoroutineScope) {
        Subspace {
            SpatialColumn {
                Volume(
                    /*
                     * the order in which Jetpack Composer’s modifier functions are called for
                     * SubspaceModifier of a Volume is very important in the Android XR app.
                     * This is what the right order of applying a chain of modifier functions
                     * should be (this may change in the future)
                     * SubspaceModifier
                     *   .offset(x, y, z)           // occurs first
                     *   .rotate(x, y, z)           // must be applied before scale
                     *   .scale(Float)
                     *   .movable()                 // must occur last
                     *   .resizable()               // must occur last
                     */

                    modifier = SubspaceModifier.offset(z = (-2000).dp).scale(0.5f).resizable()
                ) {
                    scope.launch {
                        val lf = session.createGltfResourceAsync(GLB_FILE_NAME)
                        val model = lf.await()
                        val modelEntity = session.createGltfEntity(model)
                        modelEntity.startAnimation(loop = true)
                        it.addChild(modelEntity)
                    }
                }
                SpatialPanel(
                    SubspaceModifier.height(300.dp).width(400.dp)
                ) {
                    BottomEdgeOrbiter()
                }
            }
        }
    }

    @Composable
    fun BottomEdgeOrbiter() {
        Orbiter(
            alignment = Alignment.CenterHorizontally,
            offset = 100.dp,
            position = OrbiterEdge.Bottom,
        ) {
            Surface(
                Modifier
                    .clip(CircleShape)
            ) {
                Row(
                    Modifier
                        .width(450.dp)
                        .height(100.dp)
                        .background(Color(0.25f, 0.0f, 0.0f)),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = GLB_FILE_NAME,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 6.em
                    )
                }
            }
        }
    }
}
