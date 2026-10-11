package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AiStudioSheet
import com.example.ui.components.AudioMixerSheet
import com.example.ui.components.ColorGradingPanel
import com.example.ui.components.EffectsTransitionsSheet
import com.example.ui.components.ExportStudioSheet
import com.example.ui.components.KeyframeControlBar
import com.example.ui.components.MultiTrackTimeline
import com.example.ui.components.VideoPreviewCanvas
import com.example.ui.theme.NovaAmber
import com.example.ui.theme.NovaBorder
import com.example.ui.theme.NovaCyan
import com.example.ui.theme.NovaDarkBg
import com.example.ui.theme.NovaGreen
import com.example.ui.theme.NovaOrange
import com.example.ui.theme.NovaPanelAlt
import com.example.ui.theme.NovaPanelBg
import com.example.ui.theme.NovaPink
import com.example.ui.theme.NovaPurple
import com.example.ui.theme.NovaTextPrimary
import com.example.ui.theme.NovaTextSecondary
import com.example.ui.viewmodel.EditorPanel
import com.example.ui.viewmodel.EditorViewModel

@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val project = viewModel.currentProject.collectAsState().value
    val playheadMs by viewModel.playheadMs.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val selectedClipId by viewModel.selectedClipId.collectAsState()
    val activePanel by viewModel.activePanel.collectAsState()
    val timelineZoom by viewModel.timelineZoom.collectAsState()

    // Handle back button: close bottom drawer if open, otherwise return to project list
    BackHandler {
        if (activePanel != EditorPanel.NONE) {
            viewModel.setPanel(EditorPanel.NONE)
        } else {
            onNavigateBack()
        }
    }

    if (project == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NovaDarkBg),
            contentAlignment = Alignment.Center
        ) {
            Text("No project selected", color = NovaTextSecondary)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NovaDarkBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // 1. Top Navigation & Status Bar
        EditorTopBar(
            title = project.title,
            fps = project.fps,
            resolution = project.resolutionLabel,
            isCloudSynced = project.isCloudSynced,
            onBackClick = onNavigateBack,
            onExportClick = { viewModel.setPanel(EditorPanel.EXPORT_RENDER) }
        )

        // 2. Video Preview Viewport
        VideoPreviewCanvas(
            viewModel = viewModel,
            project = project,
            playheadMs = playheadMs,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        // 3. Multi-Track Timeline
        MultiTrackTimeline(
            viewModel = viewModel,
            project = project,
            playheadMs = playheadMs,
            isPlaying = isPlaying,
            selectedClipId = selectedClipId,
            timelineZoom = timelineZoom,
            modifier = Modifier.fillMaxWidth()
        )

        // 4. Bottom Main Feature Navigation Shelf
        EditorBottomNavShelf(
            activePanel = activePanel,
            onPanelSelect = { viewModel.setPanel(it) }
        )

        // 5. Contextual Drawers / Modals (Color Grading, Keyframes, AI Studio, Audio, Effects, Clip Edit, Export)
        AnimatedVisibility(
            visible = activePanel != EditorPanel.NONE,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            when (activePanel) {
                EditorPanel.COLOR_GRADING -> {
                    ColorGradingPanel(viewModel = viewModel)
                }
                EditorPanel.KEYFRAMES -> {
                    KeyframeControlBar(viewModel = viewModel)
                }
                EditorPanel.AI_STUDIO -> {
                    AiStudioSheet(
                        viewModel = viewModel,
                        onDismiss = { viewModel.setPanel(EditorPanel.NONE) }
                    )
                }
                EditorPanel.AUDIO_MIXER -> {
                    AudioMixerSheet(
                        viewModel = viewModel,
                        onDismiss = { viewModel.setPanel(EditorPanel.NONE) }
                    )
                }
                EditorPanel.EFFECTS_TRANSITIONS -> {
                    EffectsTransitionsSheet(
                        viewModel = viewModel,
                        onDismiss = { viewModel.setPanel(EditorPanel.NONE) }
                    )
                }
                EditorPanel.EXPORT_RENDER -> {
                    ExportStudioSheet(
                        viewModel = viewModel,
                        onDismiss = { viewModel.setPanel(EditorPanel.NONE) }
                    )
                }
                EditorPanel.CLIP_EDIT -> {
                    ClipSpeedEditSheet(
                        viewModel = viewModel,
                        onDismiss = { viewModel.setPanel(EditorPanel.NONE) }
                    )
                }
                EditorPanel.NONE -> {}
            }
        }
    }
}

@Composable
private fun EditorTopBar(
    title: String,
    fps: Int,
    resolution: String,
    isCloudSynced: Boolean,
    onBackClick: () -> Unit,
    onExportClick: () -> Unit
) {
    Surface(
        color = NovaPanelBg,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(0.5.dp, NovaBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back Button & Project Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(32.dp).testTag("btn_editor_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Projects",
                        tint = NovaTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$resolution • ${fps}fps",
                            fontSize = 9.sp,
                            color = NovaTextSecondary
                        )
                        if (isCloudSynced) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = "Synced to NovaCloud",
                                tint = NovaGreen,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }
            }

            // Export CTA Button
            Button(
                onClick = onExportClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NovaCyan,
                    contentColor = NovaDarkBg
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(30.dp)
                    .testTag("btn_export_top")
            ) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Export",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EditorBottomNavShelf(
    activePanel: EditorPanel,
    onPanelSelect: (EditorPanel) -> Unit
) {
    Surface(
        color = NovaPanelBg,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .border(0.5.dp, NovaBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavShelfItem(
                label = "AI Studio",
                icon = Icons.Default.AutoAwesome,
                accentColor = NovaCyan,
                isActive = activePanel == EditorPanel.AI_STUDIO,
                onClick = { onPanelSelect(EditorPanel.AI_STUDIO) },
                testTag = "nav_ai_studio"
            )

            NavShelfItem(
                label = "Color Grade",
                icon = Icons.Default.Palette,
                accentColor = NovaAmber,
                isActive = activePanel == EditorPanel.COLOR_GRADING,
                onClick = { onPanelSelect(EditorPanel.COLOR_GRADING) },
                testTag = "nav_color_grade"
            )

            NavShelfItem(
                label = "Keyframes",
                icon = Icons.Default.Timeline,
                accentColor = NovaPink,
                isActive = activePanel == EditorPanel.KEYFRAMES,
                onClick = { onPanelSelect(EditorPanel.KEYFRAMES) },
                testTag = "nav_keyframes"
            )

            NavShelfItem(
                label = "Effects",
                icon = Icons.Default.MovieFilter,
                accentColor = NovaOrange,
                isActive = activePanel == EditorPanel.EFFECTS_TRANSITIONS,
                onClick = { onPanelSelect(EditorPanel.EFFECTS_TRANSITIONS) },
                testTag = "nav_effects"
            )

            NavShelfItem(
                label = "Audio Mixer",
                icon = Icons.Default.GraphicEq,
                accentColor = NovaGreen,
                isActive = activePanel == EditorPanel.AUDIO_MIXER,
                onClick = { onPanelSelect(EditorPanel.AUDIO_MIXER) },
                testTag = "nav_audio_mixer"
            )
        }
    }
}

@Composable
private fun NavShelfItem(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String = ""
) {
    Surface(
        onClick = onClick,
        color = if (isActive) accentColor.copy(alpha = 0.15f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        modifier = if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) accentColor else NovaTextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = if (isActive) accentColor else NovaTextSecondary
            )
        }
    }
}

@Composable
private fun ClipSpeedEditSheet(
    viewModel: EditorViewModel,
    onDismiss: () -> Unit
) {
    val selectedClip = viewModel.getSelectedClip() ?: viewModel.getActiveMainVideoClip()

    if (selectedClip == null) {
        onDismiss()
        return
    }

    var speedVal by remember { mutableFloatStateOf(selectedClip.speed) }
    var volVal by remember { mutableFloatStateOf(selectedClip.volume) }

    Surface(
        color = NovaPanelBg,
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Clip Properties: ${selectedClip.name}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NovaCyan
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Speed, contentDescription = "Close", tint = NovaTextSecondary)
                }
            }

            // Speed
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Playback Speed", fontSize = 11.sp, color = NovaTextPrimary)
                    Text("${String.format("%.1f", speedVal)}x", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NovaGreen)
                }
                Slider(
                    value = speedVal,
                    onValueChange = {
                        speedVal = it
                        viewModel.updateClipSpeed(selectedClip.id, it)
                    },
                    valueRange = 0.2f..4.0f,
                    colors = SliderDefaults.colors(thumbColor = NovaGreen, activeTrackColor = NovaGreen, inactiveTrackColor = NovaBorder)
                )
            }

            // Volume
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Audio Volume", fontSize = 11.sp, color = NovaTextPrimary)
                    Text("${(volVal * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NovaAmber)
                }
                Slider(
                    value = volVal,
                    onValueChange = {
                        volVal = it
                        viewModel.updateClipVolume(selectedClip.id, it)
                    },
                    valueRange = 0.0f..2.0f,
                    colors = SliderDefaults.colors(thumbColor = NovaAmber, activeTrackColor = NovaAmber, inactiveTrackColor = NovaBorder)
                )
            }

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NovaCyan, contentColor = NovaDarkBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done", fontWeight = FontWeight.Bold)
            }
        }
    }
}
