package com.example.xrexp.arcore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.xr.arcore.Hand
import androidx.xr.arcore.HandJointType

import androidx.xr.arcore.perceptionState
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.MainPanel
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.SubspaceComposable
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.movable
import androidx.xr.compose.subspace.layout.resizable
import androidx.xr.compose.subspace.layout.width
import androidx.xr.runtime.SessionCreateSuccess
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Vector3
import androidx.xr.scenecore.Session as SceneCoreSession
import androidx.xr.runtime.Session as ARCoreSession
import androidx.xr.scenecore.GltfModel
import com.example.xrexp.ui.theme.LocalSpacing
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator


class ExpHandsActivity : ComponentActivity() {

    companion object {
        private val TAG = "ExpArCoreActivity"
    }

    private lateinit var arCoreSession: ARCoreSession
    private lateinit var sceneCoreSession: SceneCoreSession
    private lateinit var arrowsModel : GltfModel

    private lateinit var resourceAsync : Deferred<Unit>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Subspace {
                MainPanel()
            }
        }

        arCoreSession = (ARCoreSession.create(this) as SessionCreateSuccess).session
        sceneCoreSession = SceneCoreSession.create(this)

        resourceAsync =
            lifecycleScope.async {
                arrowsModel = GltfModel.create(sceneCoreSession, "models/xyzArrows.glb").await()
            }
    }

    override fun onResume() {
        super.onResume()
        arCoreSession.resume()
        lifecycleScope.launch {
            resourceAsync.await()

            Hand.left(arCoreSession)?.state?.collect { leftHandState -> // or Hand.right(session)
                // Hand state has been updated.
                // Use the state of hand joints to update an entity's position.

//                val palmPose = leftHandState.handJoints[HandJointType.PALM] ?: return@collect
//
//                // the down direction points in the same direction as the palm
//                val angle = Vector3.angleBetween(palmPose.rotation * Vector3.Down, Vector3.Up)
//                palmEntity.setHidden(angle > Math.toRadians(40.0))
//
//                val transformedPose =
//                    sceneCoreSession.perceptionSpace.transformPoseTo(
//                        palmPose,
//                        sceneCoreSession.activitySpace,
//                    )
//                val newPosition = transformedPose.translation + transformedPose.down*0.05f
//                palmEntity.setPose(Pose(newPosition, transformedPose.rotation))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        arCoreSession.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        arCoreSession.destroy()
    }

    @Composable
    @SubspaceComposable
    private fun MainPanel() {
        val state by arCoreSession.state.collectAsStateWithLifecycle()
        val perceptionState = state.perceptionState

        SpatialPanel(
            SubspaceModifier
                .width(1000.dp)
                .height(480.dp)
                .resizable(true)
                .movable(true)
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(LocalSpacing.current.l)
            ) {
                Text(
                    modifier = Modifier.padding(LocalSpacing.current.m),
                    text = "Detected planes: ${perceptionState?.trackables?.size}"
                )
                Row {
                    HandTrackingPanel(perceptionState?.leftHand, true)
                    HandTrackingPanel(perceptionState?.rightHand, false)
                }
            }
        }
    }

    @Composable
    private fun HandTrackingPanel(hand: Hand?, isLeftHand: Boolean) {

        val name = if (isLeftHand) "Left Hand" else "Right Hand"
        val bgColor = if (isLeftHand) Color.Yellow else Color.Cyan

        if (hand == null) {
            Text("$name is NULL")
            return
        }

        val handState by hand.state.collectAsStateWithLifecycle()

        Column(
            modifier =
                Modifier.background(color = bgColor)
                    .fillMaxHeight()
                    .padding(horizontal = LocalSpacing.current.m)
        ) {
            Text("$name isActive: ${handState.isActive}")
            for ((jointType, pose) in handState.handJoints) {
                Text("$name joint ${jointType}: ${pose.translation}")
            }
        }
    }
}



