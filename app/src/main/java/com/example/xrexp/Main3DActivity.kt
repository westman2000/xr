package com.example.xrexp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.concurrent.futures.await
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.*
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.offset
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

    @Composable
    fun ModelInsideVolume(session: Session, scope: CoroutineScope) {
        Subspace {
            SpatialColumn {
                Volume(
                    modifier = SubspaceModifier.offset(z = (-2000).dp).scale(0.1f)
                ) {
                    scope.launch {
                        val lf = session.createGltfResourceAsync("phoenix_bird.glb")
                        val model = lf.await()
                        val modelEntity = session.createGltfEntity(model)
                        it.addChild(modelEntity)
                    }
                }
                SpatialPanel(
                    SubspaceModifier.height(300.dp).width(400.dp)
                ) {
//                    BottomEdgeOrbiter()
                }
            }
        }
    }
}
