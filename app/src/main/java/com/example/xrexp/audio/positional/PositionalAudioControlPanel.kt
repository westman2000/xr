package com.example.xrexp.audio.positional

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.asFloatState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.concurrent.futures.await
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.platform.LocalSpatialCapabilities
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialColumn
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.Volume
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.depth
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.offset
import androidx.xr.compose.subspace.layout.resizable
import androidx.xr.compose.subspace.layout.scale
import androidx.xr.compose.subspace.layout.size
import androidx.xr.compose.subspace.layout.width
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Vector3
import androidx.xr.scenecore.GltfModel
import androidx.xr.scenecore.GltfModelEntity
import com.example.xrexp.ui.theme.XRExpTheme
import kotlinx.coroutines.launch


@Composable
fun PositionalAudioControlPanel(
    modifier: Modifier = Modifier,
    viewModel: PositionalAudioControlViewModel = viewModel()
) {
    val uiState = viewModel.uiState.value

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Angle Slider
        PositionalAudioSliderWithTitle(
            title = "Angle",
            value = uiState.angle,
            onValueChange = { viewModel.onAngleChanged(it) },
            valueRange = 0f..360f,
            enabled = uiState.slidersEnabled
        )

        // Distance Slider
        PositionalAudioSliderWithTitle(
            title = "Distance",
            value = viewModel.distance.value,
            onValueChange = { viewModel.onDistanceChanged(it) },
            valueRange = 0f..10f,
            enabled = uiState.slidersEnabled
        )

        // Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play Button
            Button(
                onClick = { viewModel.onPlayClicked() },
                enabled = !uiState.isPlaying
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Play")
            }

            // Stop Button
            Button(
                onClick = { viewModel.onStopClicked() },
                enabled = uiState.isPlaying
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Stop"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Stop")
            }

            Spacer(modifier = Modifier.weight(1f))

            // Loop Checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.loop,
                    onCheckedChange = { viewModel.onLoopChanged(it) }
                )
                Text("Loop")
            }
        }
    }

    // Dialog
    if (uiState.showDialog) {
        Subspace {
            SpatialColumn {
                val context = LocalContext.current
                val session = LocalSession.current!!
                val coroutineScope = rememberCoroutineScope()
                val localSpatialCapabilities = LocalSpatialCapabilities.current
                val distance = viewModel.distance.asFloatState()

                Volume(
                    modifier = SubspaceModifier.scale(0.5f).resizable()
                ) {
                    coroutineScope.launch {
                        val gltfModel = GltfModel.create(session, "models/xyzArrows.glb").await()
                        // check for spatial capabilities
                        if (localSpatialCapabilities.isContent3dEnabled){
                            // create the gltf entity using the gltf file from the previous snippet
                            val gltfEntity = GltfModelEntity.create(session, gltfModel)
//                            gltfEntity.setParent(xrSession.activitySpace)
                            it.addChild(gltfEntity)

                            gltfEntity.setPose(Pose(translation = Vector3(0f, 0f, distance.floatValue)))

                        } else {
                            Toast.makeText(context, "3D content not enabled", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                SpatialPanel(
                    SubspaceModifier.size(1000.dp)
                ) {
                    Log.d("TAG", "PositionalAudioControlPanel: ${uiState.showDialog} - ${distance.floatValue}")
                }
            }
        }
    }
}

@Composable
fun PositionalEntityPanel(
    viewModel: PositionalAudioControlViewModel = viewModel()
) {

    val uiState = viewModel.uiState.value

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Animation in Progress",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Angle: ${uiState.angle.toInt()}°\nDistance: ${"%.1f".format(viewModel.distance.value)} units",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.onDismissDialog() }
            ) {
                Text("Close")
            }
        }
    }
}

@Composable
fun PositionalAudioSliderWithTitle(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    enabled: Boolean
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = if (title == "Angle") "${value.toInt()}°" else "${"%.1f".format(value)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            enabled = enabled,
            steps = if (title == "Angle") 36 else 10
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ControlPanelPreview() {
    XRExpTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PositionalAudioControlPanel()
        }
    }
}