package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EasingType
import com.example.ui.theme.NovaAmber
import com.example.ui.theme.NovaBorder
import com.example.ui.theme.NovaCyan
import com.example.ui.theme.NovaDarkBg
import com.example.ui.theme.NovaGreen
import com.example.ui.theme.NovaPanelAlt
import com.example.ui.theme.NovaPanelBg
import com.example.ui.theme.NovaPink
import com.example.ui.theme.NovaPurple
import com.example.ui.theme.NovaTextPrimary
import com.example.ui.theme.NovaTextSecondary
import com.example.ui.viewmodel.EditorViewModel

@Composable
fun KeyframeControlBar(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val selectedClip = viewModel.getSelectedClip() ?: viewModel.getActiveMainVideoClip()
    val playheadMs = viewModel.playheadMs.value

    if (selectedClip == null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(NovaPanelBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Select a clip to animate motion keyframes",
                color = NovaTextSecondary,
                fontSize = 13.sp
            )
        }
        return
    }

    val interpolated = viewModel.interpolateKeyframeAt(selectedClip, playheadMs)
    val hasKeyframeAtPlayhead = selectedClip.keyframes.any { Math.abs(it.timeMs - playheadMs) < 150L }

    var easingMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp)
            .background(NovaPanelBg)
            .padding(12.dp)
    ) {
        // Keyframe Toolbar Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timeline,
                    contentDescription = null,
                    tint = NovaAmber,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MOTION GRAPHICS & KEYFRAMES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NovaAmber,
                    letterSpacing = 0.5.sp
                )
            }

            // Add/Toggle Diamond Keyframe Button
            Button(
                onClick = { viewModel.addOrToggleKeyframeAtPlayhead() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasKeyframeAtPlayhead) NovaAmber else NovaPanelAlt,
                    contentColor = if (hasKeyframeAtPlayhead) NovaDarkBg else NovaAmber
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("btn_toggle_keyframe")
            ) {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = "Keyframe Diamond",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (hasKeyframeAtPlayhead) "Remove Keyframe" else "+ Add Keyframe",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Easing interpolation selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Easing Curve:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = NovaTextSecondary
            )

            Box {
                OutlinedButton(
                    onClick = { easingMenuExpanded = true },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = interpolated.easing.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaCyan
                    )
                }

                DropdownMenu(
                    expanded = easingMenuExpanded,
                    onDismissRequest = { easingMenuExpanded = false }
                ) {
                    EasingType.entries.forEach { easing ->
                        DropdownMenuItem(
                            text = { Text(easing.label) },
                            onClick = {
                                viewModel.updateKeyframeValues(easing = easing)
                                easingMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Scrollable Keyframe Value Sliders
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Scale Slider
            KeyframeSlider(
                label = "Scale",
                value = interpolated.scale,
                range = 0.2f..3.0f,
                display = String.format("%.2fx", interpolated.scale),
                activeColor = NovaCyan,
                onValueChange = { viewModel.updateKeyframeValues(scale = it) }
            )

            // Position X
            KeyframeSlider(
                label = "Position X",
                value = interpolated.posX,
                range = -1.0f..1.0f,
                display = String.format("%.2f", interpolated.posX),
                activeColor = NovaGreen,
                onValueChange = { viewModel.updateKeyframeValues(posX = it) }
            )

            // Position Y
            KeyframeSlider(
                label = "Position Y",
                value = interpolated.posY,
                range = -1.0f..1.0f,
                display = String.format("%.2f", interpolated.posY),
                activeColor = NovaGreen,
                onValueChange = { viewModel.updateKeyframeValues(posY = it) }
            )

            // Rotation
            KeyframeSlider(
                label = "Rotation",
                value = interpolated.rotation,
                range = -180f..180f,
                display = "${interpolated.rotation.toInt()}°",
                activeColor = NovaPink,
                onValueChange = { viewModel.updateKeyframeValues(rotation = it) }
            )

            // Opacity
            KeyframeSlider(
                label = "Opacity",
                value = interpolated.opacity,
                range = 0.0f..1.0f,
                display = "${(interpolated.opacity * 100).toInt()}%",
                activeColor = NovaPurple,
                onValueChange = { viewModel.updateKeyframeValues(opacity = it) }
            )
        }
    }
}

@Composable
private fun KeyframeSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    display: String,
    activeColor: Color,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = NovaTextPrimary,
            modifier = Modifier.width(80.dp)
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = activeColor,
                activeTrackColor = activeColor,
                inactiveTrackColor = NovaBorder
            ),
            modifier = Modifier
                .weight(1f)
                .height(26.dp)
        )

        Text(
            text = display,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = activeColor,
            modifier = Modifier
                .width(45.dp)
                .padding(start = 6.dp)
        )
    }
}
