package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioTrack
import com.example.data.model.ClipItem
import com.example.data.model.Project
import com.example.data.model.TrackType
import com.example.ui.theme.AudioTrackColor
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
import com.example.ui.theme.NovaTrackBg
import com.example.ui.theme.PipTrackColor
import com.example.ui.theme.SfxTrackColor
import com.example.ui.theme.TextTrackColor
import com.example.ui.theme.VideoTrackColor
import com.example.ui.viewmodel.EditorPanel
import com.example.ui.viewmodel.EditorViewModel

@Composable
fun MultiTrackTimeline(
    viewModel: EditorViewModel,
    project: Project,
    playheadMs: Long,
    isPlaying: Boolean,
    selectedClipId: String?,
    timelineZoom: Float,
    modifier: Modifier = Modifier
) {
    val totalDurationMs = project.totalDurationMs.coerceAtLeast(10_000L)
    // Zoom factor: pixels per millisecond
    val pxPerMs = 0.08f * timelineZoom
    val timelineWidthDp = (totalDurationMs * pxPerMs).dp.coerceAtLeast(600.dp)

    val scrollState = rememberScrollState()

    // Auto-scroll timeline to follow playhead during playback
    LaunchedEffect(playheadMs, isPlaying) {
        if (isPlaying) {
            val targetScroll = (playheadMs * pxPerMs * 2.5f).toInt()
            if (targetScroll > scrollState.value + 400 || targetScroll < scrollState.value) {
                scrollState.scrollTo((targetScroll - 200).coerceAtLeast(0))
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NovaPanelBg)
    ) {
        // Timeline Header: Timecode readout, Zoom slider, Playback transport
        TimelineTransportBar(
            viewModel = viewModel,
            playheadMs = playheadMs,
            totalDurationMs = totalDurationMs,
            isPlaying = isPlaying,
            timelineZoom = timelineZoom
        )

        // Quick Timeline Action Shelf (Split, Delete, Duplicate, Keyframe)
        TimelineActionShelf(
            viewModel = viewModel,
            selectedClipId = selectedClipId
        )

        // Multi-Track Timeline Viewport
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .height(210.dp)
                .background(NovaDarkBg)
        ) {
            // Horizontal scrollable area
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .width(timelineWidthDp)
                        .fillMaxHeight()
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // 1. Timecode Ruler & Beat Ticks
                        TimelineRuler(
                            totalDurationMs = totalDurationMs,
                            pxPerMs = pxPerMs,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(26.dp)
                                .pointerInput(Unit) {
                                    detectHorizontalDragGestures { change, _ ->
                                        change.consume()
                                        val newTime = (change.position.x / pxPerMs).toLong()
                                        viewModel.seekTo(newTime)
                                    }
                                }
                        )

                        // 2. Track 1: Main Video (A-Roll)
                        TimelineTrackRow(
                            trackType = TrackType.VIDEO_MAIN,
                            clips = project.getClipsForTrack(TrackType.VIDEO_MAIN),
                            selectedClipId = selectedClipId,
                            pxPerMs = pxPerMs,
                            onClipClick = { viewModel.selectClip(it.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        )

                        // 3. Track 2: Overlay / PIP (B-Roll)
                        TimelineTrackRow(
                            trackType = TrackType.VIDEO_PIP,
                            clips = project.getClipsForTrack(TrackType.VIDEO_PIP),
                            selectedClipId = selectedClipId,
                            pxPerMs = pxPerMs,
                            onClipClick = { viewModel.selectClip(it.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                        )

                        // 4. Track 3: Text & Captions
                        TimelineTrackRow(
                            trackType = TrackType.TEXT_GRAPHICS,
                            clips = project.getClipsForTrack(TrackType.TEXT_GRAPHICS),
                            selectedClipId = selectedClipId,
                            pxPerMs = pxPerMs,
                            onClipClick = { viewModel.selectClip(it.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                        )

                        // 5. Track 4: Audio Track (BGM)
                        TimelineTrackRow(
                            trackType = TrackType.AUDIO_BGM,
                            clips = project.getClipsForTrack(TrackType.AUDIO_BGM),
                            selectedClipId = selectedClipId,
                            pxPerMs = pxPerMs,
                            onClipClick = { viewModel.selectClip(it.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                        )

                        // 6. Track 5: Audio SFX
                        TimelineTrackRow(
                            trackType = TrackType.AUDIO_SFX,
                            clips = project.getClipsForTrack(TrackType.AUDIO_SFX),
                            selectedClipId = selectedClipId,
                            pxPerMs = pxPerMs,
                            onClipClick = { viewModel.selectClip(it.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(28.dp)
                        )
                    }

                    // Draggable Needle Playhead
                    val playheadX = (playheadMs * pxPerMs).dp
                    PlayheadNeedle(
                        modifier = Modifier
                            .offset(x = playheadX - 10.dp)
                            .fillMaxHeight()
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val deltaMs = (dragAmount.x / pxPerMs).toLong()
                                    viewModel.seekTo(playheadMs + deltaMs)
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineTransportBar(
    viewModel: EditorViewModel,
    playheadMs: Long,
    totalDurationMs: Long,
    isPlaying: Boolean,
    timelineZoom: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Timecode display
        val curTimecode = formatTimecode(playheadMs)
        val totalTimecode = formatTimecode(totalDurationMs)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = curTimecode,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = NovaCyan
            )
            Text(
                text = " / $totalTimecode",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = NovaTextSecondary
            )
        }

        // Center Transport Controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Jump to Prev Cut
            IconButton(
                onClick = { viewModel.jumpToPrevCut() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FastRewind,
                    contentDescription = "Previous Cut",
                    tint = NovaTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Step 1 Frame Back
            IconButton(
                onClick = { viewModel.stepFrame(false) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Step 1 Frame Back",
                    tint = NovaTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Play / Pause Button
            Surface(
                onClick = { viewModel.togglePlayPause() },
                shape = CircleShape,
                color = NovaCyan,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("btn_play_pause")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = NovaDarkBg,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Step 1 Frame Forward
            IconButton(
                onClick = { viewModel.stepFrame(true) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Step 1 Frame Forward",
                    tint = NovaTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Jump to Next Cut
            IconButton(
                onClick = { viewModel.jumpToNextCut() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "Next Cut",
                    tint = NovaTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Zoom stepper
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { viewModel.setTimelineZoom(timelineZoom - 0.25f) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomOut,
                    contentDescription = "Zoom Out",
                    tint = NovaTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = "${(timelineZoom * 100).toInt()}%",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NovaTextSecondary
            )
            IconButton(
                onClick = { viewModel.setTimelineZoom(timelineZoom + 0.25f) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = "Zoom In",
                    tint = NovaTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun TimelineActionShelf(
    viewModel: EditorViewModel,
    selectedClipId: String?
) {
    Surface(
        color = NovaPanelAlt,
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Split at Playhead
            TimelineActionButton(
                label = "Split",
                icon = Icons.Default.ContentCut,
                color = NovaCyan,
                onClick = { viewModel.splitClipAtPlayhead() },
                testTag = "btn_split_clip"
            )

            // Delete Clip
            TimelineActionButton(
                label = "Delete",
                icon = Icons.Default.Delete,
                color = NovaPink,
                enabled = selectedClipId != null,
                onClick = { viewModel.deleteSelectedClip() },
                testTag = "btn_delete_clip"
            )

            // Duplicate Clip
            TimelineActionButton(
                label = "Duplicate",
                icon = Icons.Default.ContentCopy,
                color = NovaTextPrimary,
                enabled = selectedClipId != null,
                onClick = { viewModel.duplicateSelectedClip() },
                testTag = "btn_duplicate_clip"
            )

            // Add/Toggle Keyframe at Playhead
            TimelineActionButton(
                label = "Keyframe",
                icon = Icons.Default.Add,
                color = NovaAmber,
                onClick = { viewModel.addOrToggleKeyframeAtPlayhead() },
                testTag = "btn_add_keyframe"
            )

            // Audio & Speed quick access
            TimelineActionButton(
                label = "Speed",
                icon = Icons.Default.Speed,
                color = NovaGreen,
                onClick = { viewModel.setPanel(EditorPanel.CLIP_EDIT) }
            )
        }
    }
}

@Composable
private fun TimelineActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    enabled: Boolean = true,
    onClick: () -> Unit,
    testTag: String = ""
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        color = Color.Transparent,
        shape = RoundedCornerShape(6.dp),
        modifier = if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (enabled) color else NovaTextSecondary.copy(alpha = 0.4f),
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) color else NovaTextSecondary.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun TimelineRuler(
    totalDurationMs: Long,
    pxPerMs: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Background
        drawRect(color = Color(0xFF131520))

        // Seconds ticks
        val totalSeconds = (totalDurationMs / 1000L).toInt()
        for (sec in 0..totalSeconds) {
            val x = sec * 1000f * pxPerMs

            // Major tick on every second
            drawLine(
                color = NovaTextSecondary.copy(alpha = 0.6f),
                start = Offset(x, h - 12f),
                end = Offset(x, h),
                strokeWidth = 1.5f
            )

            // Minor ticks on half seconds
            val halfX = x + (500f * pxPerMs)
            drawLine(
                color = NovaTextSecondary.copy(alpha = 0.3f),
                start = Offset(halfX, h - 6f),
                end = Offset(halfX, h),
                strokeWidth = 1f
            )
        }

        // Bottom divider line
        drawLine(
            color = NovaBorder,
            start = Offset(0f, h),
            end = Offset(w, h),
            strokeWidth = 1f
        )
    }
}

@Composable
private fun TimelineTrackRow(
    trackType: TrackType,
    clips: List<ClipItem>,
    selectedClipId: String?,
    pxPerMs: Float,
    onClipClick: (ClipItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(0.5.dp, NovaBorder.copy(alpha = 0.5f))
            .background(NovaTrackBg)
    ) {
        clips.forEach { clip ->
            val startX = (clip.startMs * pxPerMs).dp
            val clipWidth = ((clip.endMs - clip.startMs) * pxPerMs).dp.coerceAtLeast(24.dp)
            val isSelected = clip.id == selectedClipId

            TimelineClipPill(
                clip = clip,
                isSelected = isSelected,
                onClick = { onClipClick(clip) },
                modifier = Modifier
                    .offset(x = startX)
                    .width(clipWidth)
                    .fillMaxHeight()
                    .padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun TimelineClipPill(
    clip: ClipItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val trackColor = when (clip.trackType) {
        TrackType.VIDEO_MAIN -> VideoTrackColor
        TrackType.VIDEO_PIP -> PipTrackColor
        TrackType.TEXT_GRAPHICS -> TextTrackColor
        TrackType.AUDIO_BGM -> AudioTrackColor
        TrackType.AUDIO_SFX -> SfxTrackColor
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        color = trackColor.copy(alpha = if (isSelected) 0.95f else 0.75f),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, NovaCyan) else null,
        modifier = modifier
            .shadow(if (isSelected) 4.dp else 0.dp, RoundedCornerShape(4.dp))
            .testTag("clip_${clip.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Audio Waveform for audio clips
            if (clip.audioTrack != null) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val wave = clip.audioTrack.waveformData
                    val barW = size.width / wave.size.toFloat()
                    wave.forEachIndexed { i, amp ->
                        val barH = size.height * amp * 0.8f
                        drawRect(
                            color = Color.White.copy(alpha = 0.45f),
                            topLeft = Offset(i * barW, (size.height - barH) / 2f),
                            size = Size(barW * 0.75f, barH)
                        )
                    }
                }
            }

            // Clip Title & Speed badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = clip.name,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (clip.speed != 1.0f) {
                    Text(
                        text = "${clip.speed}x",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaAmber,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }

            // Keyframe diamond markers inside clip
            if (clip.keyframes.isNotEmpty()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val clipDuration = (clip.endMs - clip.startMs).toFloat()
                    clip.keyframes.forEach { kf ->
                        val kfOffset = (kf.timeMs - clip.startMs).toFloat()
                        if (kfOffset in 0f..clipDuration) {
                            val kfX = (kfOffset / clipDuration) * size.width
                            // Draw diamond
                            val path = androidx.compose.ui.graphics.Path().apply {
                                moveTo(kfX, size.height / 2f - 4f)
                                lineTo(kfX + 4f, size.height / 2f)
                                lineTo(kfX, size.height / 2f + 4f)
                                lineTo(kfX - 4f, size.height / 2f)
                                close()
                            }
                            drawPath(path, color = NovaAmber)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayheadNeedle(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Needle Head (inverted pentagon / scrubber knob)
        Canvas(modifier = Modifier.size(20.dp, 16.dp)) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(2f, 0f)
                lineTo(size.width - 2f, 0f)
                lineTo(size.width - 2f, 10f)
                lineTo(size.width / 2f, size.height)
                lineTo(2f, 10f)
                close()
            }
            drawPath(path, color = NovaCyan)
        }

        // Needle Line (Glowing laser indicator)
        Box(
            modifier = Modifier
                .width(2.5.dp)
                .weight(1f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(NovaCyan, NovaCyan.copy(alpha = 0.9f))
                    )
                )
        )
    }
}

private fun formatTimecode(ms: Long): String {
    val totalSec = ms / 1000L
    val minutes = totalSec / 60L
    val seconds = totalSec % 60L
    val frames = ((ms % 1000L) / 33.33f).toInt()
    return String.format("%02d:%02d:%02d", minutes, seconds, frames)
}
