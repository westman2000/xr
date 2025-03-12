package com.example.xrexp.arcore.thumbsup

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.xr.runtime.math.Vector3


/**
 * Main debug visualization composable that displays hand tracking data
 */
@Composable
fun HandGestureDebugVisualization(
    debugInfo: ThumbsUpDetector.DebugInfo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "DEBUG INFO",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Hand tracking status
        StatusCard(
            title = "Tracking Status",
            isActive = debugInfo.isActive && debugInfo.hasAllRequiredJoints
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Threshold values and current measurements
        ThresholdDisplay(
            title = "Thumb Up Alignment",
            currentValue = debugInfo.thumbUpAlignment,
            threshold = 0.7f, // Same as in detector
            isAboveThresholdGood = true,
            description = "How well thumb aligns with 'up' direction"
        )

        Spacer(modifier = Modifier.height(8.dp))

        ThresholdDisplay(
            title = "Thumb Extension",
            currentValue = debugInfo.thumbExtensionRatio,
            threshold = 1.5f, // Same as in detector
            isAboveThresholdGood = true,
            description = "Ratio of thumb tip to base distances"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Finger curl visualization
        Text(
            text = "Finger Curl Status",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        FingerCurlVisualization(
            fingerCurlValues = debugInfo.fingerCurlValues,
            threshold = 0.7f // Same as in detector
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Thumbs up result
        val isThumbsUp = debugInfo.isThumbPointingUp &&
                debugInfo.isThumbExtended &&
                debugInfo.isIndexCurled &&
                debugInfo.isMiddleCurled &&
                debugInfo.isRingCurled &&
                debugInfo.isLittleCurled

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isThumbsUp) Color(0xFF4CAF50) else Color(0xFFE0E0E0)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isThumbsUp) "👍 THUMBS UP DETECTED!" else "No thumbs up detected",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isThumbsUp) Color.White else Color.DarkGray
                    )
                )
            }
        }
    }
}

/**
 * Status card that shows if hand tracking is active
 */
@Composable
fun StatusCard(title: String, isActive: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color(0xFF4CAF50) else Color(0xFFE57373)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = if (isActive) "ACTIVE" else "INACTIVE",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

/**
 * Displays current value against threshold
 */
@Composable
fun ThresholdDisplay(
    title: String,
    currentValue: Float,
    threshold: Float,
    isAboveThresholdGood: Boolean,
    description: String
) {
    val isGood = if (isAboveThresholdGood) currentValue >= threshold else currentValue <= threshold

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = String.format("%.2f", currentValue),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGood) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                )

                Text(
                    text = " / ${String.format("%.2f", threshold)}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress indicator
            LinearProgressIndicator(
                progress = { currentValue / (threshold * 1.5f) },
                modifier = Modifier.fillMaxWidth(),
                color = if (isGood) Color(0xFF4CAF50) else Color(0xFFF44336),
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            )
        }
    }
}

/**
 * Visualization of finger curl values
 */
@Composable
fun FingerCurlVisualization(
    fingerCurlValues: Map<String, Float>,
    threshold: Float
) {
    val fingerOrder = listOf("index", "middle", "ring", "little")
    val fingerNames = mapOf(
        "index" to "Index",
        "middle" to "Middle",
        "ring" to "Ring",
        "little" to "Little"
    )

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            fingerOrder.forEach { finger ->
                val value = fingerCurlValues[finger] ?: 0f
                val isCurled = value < threshold

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = fingerNames[finger] ?: finger,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.width(60.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    LinearProgressIndicator(
                        progress = { value },
                        modifier = Modifier.weight(1f),
                        color = if (isCurled) Color(0xFF4CAF50) else Color(0xFFF44336),
                        trackColor = Color(0xFFE0E0E0)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = String.format("%.2f", value),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isCurled) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = if (isCurled) "✓" else "✗",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurled) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Values < $threshold indicate curled fingers (good for thumbs up)",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            )
        }
    }
}

/**
 * Draw scope extension to draw vectors
 */
private fun DrawScope.drawVector(
    center: Offset,
    vector: Vector3,
    scale: Float,
    color: Color,
    label: String
) {
    // Project 3D vector to 2D
    val end = Offset(
        center.x + vector.x * scale,
        center.y - vector.y * scale  // Invert Y for screen coords
    )

    // Draw the vector as a line
    drawLine(
        color = color,
        start = center,
        end = end,
        strokeWidth = 5f,
        cap = StrokeCap.Round
    )

    // Draw arrowhead
    val arrowSize = 10f
    drawCircle(
        color = color,
        radius = arrowSize,
        center = end
    )
}

/**
 * Draw scope extension to draw a simplified hand
 */
private fun DrawScope.drawHand(
    center: Offset,
    scale: Float,
    isLeftHand: Boolean
) {
    val handColor = Color(0xFFE0E0E0)
    val outlineColor = Color(0xFF9E9E9E)
    val xDirection = if (isLeftHand) -1 else 1

    // Draw palm
    drawCircle(
        color = handColor,
        radius = scale * 0.5f,
        center = center,
        style = Stroke(width = 2f)
    )

    // Draw wrist line
    drawLine(
        color = outlineColor,
        start = Offset(center.x - scale * 0.4f * xDirection, center.y + scale * 0.4f),
        end = Offset(center.x + scale * 0.4f * xDirection, center.y + scale * 0.4f),
        strokeWidth = 3f
    )

    // Draw thumb (simplified)
    drawLine(
        color = outlineColor,
        start = center,
        end = Offset(center.x - scale * 0.6f * xDirection, center.y - scale * 0.2f),
        strokeWidth = 3f
    )

    // Draw fingers (simplified)
    val fingerStartY = center.y - scale * 0.2f
    val fingerEndY = center.y - scale * 0.8f

    // Index
    drawLine(
        color = outlineColor,
        start = Offset(center.x - scale * 0.3f * xDirection, fingerStartY),
        end = Offset(center.x - scale * 0.3f * xDirection, fingerEndY),
        strokeWidth = 3f
    )

    // Middle
    drawLine(
        color = outlineColor,
        start = Offset(center.x - scale * 0.1f * xDirection, fingerStartY),
        end = Offset(center.x - scale * 0.1f * xDirection, fingerEndY - scale * 0.1f),
        strokeWidth = 3f
    )

    // Ring
    drawLine(
        color = outlineColor,
        start = Offset(center.x + scale * 0.1f * xDirection, fingerStartY),
        end = Offset(center.x + scale * 0.1f * xDirection, fingerEndY - scale * 0.05f),
        strokeWidth = 3f
    )

    // Little
    drawLine(
        color = outlineColor,
        start = Offset(center.x + scale * 0.3f * xDirection, fingerStartY),
        end = Offset(center.x + scale * 0.3f * xDirection, fingerEndY - scale * 0.2f),
        strokeWidth = 3f
    )
}

/**
 * Example usage in your activity or fragment
 */
@Composable
fun HandGestureDebugScreen() {
    // State holders for debug info
    var leftHandDebugInfo by remember { mutableStateOf<ThumbsUpDetector.DebugInfo?>(null) }
    var rightHandDebugInfo by remember { mutableStateOf<ThumbsUpDetector.DebugInfo?>(null) }

    // This effect would collect hand tracking data
    LaunchedEffect(Unit) {
        // Start collecting from hand tracking
        // Example:
        // Hand.left(session).state.collect { leftHandState ->
        //     val result = ThumbsUpDetector.detectThumbsUp(leftHandState, includeDebugInfo = true)
        //     leftHandDebugInfo = result.debugInfo
        // }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Hand Gesture Debug",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // We create a TabRow to switch between hands
        var selectedTabIndex by remember { mutableStateOf(0) }

        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Left Hand") }
            )

            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Right Hand") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show the selected hand debug info
        when (selectedTabIndex) {
            0 -> {
                // Left hand
                leftHandDebugInfo?.let { debugInfo ->
                    HandGestureDebugVisualization(
                        debugInfo = debugInfo,
                    )
                } ?: Text("No left hand tracking data available")
            }

            1 -> {
                // Right hand
                rightHandDebugInfo?.let { debugInfo ->
                    HandGestureDebugVisualization(
                        debugInfo = debugInfo,
                    )
                } ?: Text("No right hand tracking data available")
            }
        }
    }
}