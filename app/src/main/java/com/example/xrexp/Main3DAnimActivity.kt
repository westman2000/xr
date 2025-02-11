package com.example.xrexp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.example.xrexp.ui.theme.XRExpTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.xr.scenecore.Entity
import androidx.xr.scenecore.GltfModelEntity
import androidx.xr.scenecore.InputEvent
import androidx.xr.scenecore.Session
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Quaternion
import androidx.xr.runtime.math.Vector3
import java.util.concurrent.Executors


class Main3DAnimActivity : ComponentActivity() {

    companion object {
        const val GLB_FILE_NAME = "shiba_inu_texture_updated.glb"
    }

    private lateinit var GLB : GltfModelEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val session = LocalSession.current!!
            XRExpTheme {
                AnimatedSceneInFullSpace(session)
            }
        }

    }

    @Composable
    fun AnimatedSceneInFullSpace(session: Session) {
        val root: Entity = session.activitySpaceRoot
        val executor by lazy { Executors.newSingleThreadExecutor() }

        Subspace {
            LaunchedEffect(Unit) {
                // Model Loading
                val lf = session.createGltfResourceAsync(GLB_FILE_NAME)
                val model = lf.await()
                val modelEntity = session.createGltfEntity(model)

                // Global pty initialization
                GLB = modelEntity

                // Transformations
                val translation = Vector3(0.5f, -2.7f, -2.4f)
                val orientation = Quaternion.fromEulerAngles(0f, -90f, 0f)
                val pose = Pose(translation, orientation)
                modelEntity.setPose(pose)
                modelEntity.setScale(1.5f)

                // Playing and stopping Animation
                modelEntity.startAnimation(loop = false, animationName = "sitting_skeletal.3")
                modelEntity.stopAnimation()

                // Setting an Interactable Component
                val interactable = session.createInteractableComponent(executor) { ie ->
                    when (ie.action) {
                        InputEvent.ACTION_HOVER_ENTER -> {
                            modelEntity.setScale(2.7f)
                            println(modelEntity.getScale())
                        }
                        InputEvent.ACTION_HOVER_EXIT -> {
                            modelEntity.setScale(1.5f)
                            println(modelEntity.getScale())
                        }
                    }
                }
                modelEntity.addComponent(interactable)
                modelEntity.setParent(root)
            }
            SpatialPanel(
                SubspaceModifier
                    .height(80.dp)
                    .width(320.dp)
                    .offset(x = 50.dp, y = 120.dp)
            ) {
                AnimationSwitch()
            }
        }
    }

    @Composable
    private fun ButtonPrototype(
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
    private fun AnimationSwitch() {

        /*
        *  "Static Pose"
        *   "play_dead_skeletal.3"
        *   "rollover_skeletal.3"
        *   "shake_skeletal.3"
        *  "sitting_skeletal.3"
        *   "standing_skeletal.3"
         */

        Surface(
            Modifier.clip(CircleShape)
        ) {
            Row(
                Modifier.width(IntrinsicSize.Min)
            ) {
                ButtonPrototype(tint = Color.Magenta) {
                    GLB.startAnimation(loop = true, animationName = "standing_skeletal.3")
                }
                ButtonPrototype(tint = Color.Green) {
                    GLB.startAnimation(loop = true, animationName = "rollover_skeletal.3")
                }
                ButtonPrototype(tint = Color.Yellow) {
                    GLB.startAnimation(loop = true, animationName = "shake_skeletal.3")
                }
                ButtonPrototype(tint = Color.Cyan) {
                    GLB.startAnimation(loop = true, animationName = "play_dead_skeletal.3")
                }
            }
        }
    }
}
