package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioTrack
import com.example.data.repository.MediaLibraryRepository
import com.example.ui.theme.AudioTrackColor
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
import com.example.ui.theme.SfxTrackColor
import com.example.ui.theme.VideoTrackColor
import com.example.ui.viewmodel.EditorViewModel

@Composable
fun AudioMixerSheet(
    viewModel: EditorViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Trending Music", "Multi-Track Mixer", "Sound Effects SFX")

    val project = viewModel.currentProject.value

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(370.dp)
            .background(NovaPanelBg)
            .padding(top = 10.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = AudioTrackColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AUDIO STUDIO & MIXER",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NovaTextPrimary,
                    letterSpacing = 1.sp
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = NovaTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = NovaPanelBg,
            contentColor = AudioTrackColor,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = AudioTrackColor,
                    height = 2.dp
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) AudioTrackColor else NovaTextSecondary
                        )
                    }
                )
            }
        }

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            when (selectedTab) {
                0 -> TrendingMusicTab(viewModel = viewModel)
                1 -> MultiTrackMixerTab(viewModel = viewModel)
                2 -> SoundEffectsTab(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun TrendingMusicTab(
    viewModel: EditorViewModel
) {
    val audioList = MediaLibraryRepository.trendingAudioList

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        audioList.forEach { track ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NovaPanelAlt,
                border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cover Art icon
                    Surface(
                        color = Color(track.coverColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = track.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NovaTextPrimary
                            )
                            if (track.isTrending) {
                                Surface(
                                    color = NovaPink,
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.padding(start = 6.dp)
                                ) {
                                    Text(
                                        text = "HOT",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${track.artist} • ${track.bpm} BPM • ${track.durationMs / 1000}s",
                            fontSize = 10.sp,
                            color = NovaTextSecondary
                        )

                        // Mini Waveform
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .padding(top = 4.dp)
                        ) {
                            val w = size.width
                            val step = w / track.waveformData.size
                            track.waveformData.forEachIndexed { i, amp ->
                                val barH = size.height * amp
                                drawRect(
                                    color = NovaCyan.copy(alpha = 0.7f),
                                    topLeft = Offset(i * step, size.height - barH),
                                    size = Size(step * 0.7f, barH)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = { viewModel.addTrendingAudio(track) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AudioTrackColor,
                            contentColor = NovaDarkBg
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MultiTrackMixerTab(
    viewModel: EditorViewModel
) {
    val project = viewModel.currentProject.value ?: return

    var videoVol by remember { mutableFloatStateOf(1.0f) }
    var bgmVol by remember { mutableFloatStateOf(0.85f) }
    var sfxVol by remember { mutableFloatStateOf(1.0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Audio Ducking Card
        Card(
            colors = CardDefaults.cardColors(containerColor = NovaPanelAlt),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Smart Audio Ducking",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaTextPrimary
                    )
                    Text(
                        text = "Lowers background music automatically when talking/voice is active",
                        fontSize = 9.sp,
                        color = NovaTextSecondary
                    )
                }

                Switch(
                    checked = project.audioDuckingEnabled,
                    onCheckedChange = { viewModel.toggleAudioDucking() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NovaDarkBg,
                        checkedTrackColor = NovaCyan
                    )
                )
            }
        }

        // Faders
        VolumeFaderRow(
            label = "Video Audio (A-Roll)",
            value = videoVol,
            trackColor = VideoTrackColor,
            onValueChange = { videoVol = it }
        )

        VolumeFaderRow(
            label = "Music Audio (BGM)",
            value = bgmVol,
            trackColor = AudioTrackColor,
            onValueChange = { bgmVol = it }
        )

        VolumeFaderRow(
            label = "Sound Effects (SFX)",
            value = sfxVol,
            trackColor = SfxTrackColor,
            onValueChange = { sfxVol = it }
        )
    }
}

@Composable
private fun VolumeFaderRow(
    label: String,
    value: Float,
    trackColor: Color,
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
            modifier = Modifier.width(130.dp)
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.0f..2.0f,
            colors = SliderDefaults.colors(
                thumbColor = trackColor,
                activeTrackColor = trackColor,
                inactiveTrackColor = NovaBorder
            ),
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "${(value * 100).toInt()}%",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = trackColor,
            modifier = Modifier
                .width(42.dp)
                .padding(start = 6.dp)
        )
    }
}

@Composable
private fun SoundEffectsTab(
    viewModel: EditorViewModel
) {
    val sfxList = MediaLibraryRepository.soundEffectsList

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Viral Sound Effects Library",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NovaTextSecondary
        )

        sfxList.forEach { (name, tag) ->
            Surface(
                onClick = { viewModel.addSoundEffect(name) },
                shape = RoundedCornerShape(8.dp),
                color = NovaPanelAlt,
                border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = SfxTrackColor.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = SfxTrackColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = NovaTextPrimary
                        )
                    }

                    Text(
                        text = "+ Drop at Playhead",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaCyan
                    )
                }
            }
        }
    }
}
