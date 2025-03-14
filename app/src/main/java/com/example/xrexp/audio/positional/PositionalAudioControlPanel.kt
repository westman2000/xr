package com.example.xrexp.audio.positional

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.xr.compose.spatial.SpatialDialog
import androidx.xr.compose.spatial.SpatialDialogProperties
import com.example.xrexp.ui.theme.XRExpTheme


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
            value = uiState.distance,
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
        SpatialDialog(
            onDismissRequest = { viewModel.onDismissDialog() },
            properties = SpatialDialogProperties(dismissOnClickOutside = false)
        ) {
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
                        text = "Angle: ${uiState.angle.toInt()}°\nDistance: ${"%.1f".format(uiState.distance)} units",
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