package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiAnalysisResult
import com.example.data.model.CaptionStylePreset
import com.example.data.model.RoughCutPreset
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
import com.example.ui.viewmodel.AiStatus
import com.example.ui.viewmodel.EditorViewModel

@Composable
fun AiStudioSheet(
    viewModel: EditorViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("AI Auto-Rough Cut", "AI Auto-Captions", "AI Cinematic Grade")

    val aiStatus by viewModel.aiStatus.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp)
            .background(NovaPanelBg)
            .padding(top = 10.dp)
    ) {
        // Sheet Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = NovaCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI INTELLIGENCE STUDIO",
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
                    contentDescription = "Close AI Studio",
                    tint = NovaTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = NovaPanelBg,
            contentColor = NovaCyan,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NovaCyan,
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
                            color = if (selectedTab == index) NovaCyan else NovaTextSecondary
                        )
                    }
                )
            }
        }

        // Body Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            when (selectedTab) {
                0 -> AiRoughCutTab(viewModel = viewModel, aiStatus = aiStatus)
                1 -> AiCaptionsTab(viewModel = viewModel)
                2 -> AiCinematicGradeTab(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun AiRoughCutTab(
    viewModel: EditorViewModel,
    aiStatus: AiStatus
) {
    var selectedPreset by remember { mutableStateOf(RoughCutPreset.TIKTOK_15S) }

    when (aiStatus) {
        is AiStatus.Idle -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Social Platform Optimization Preset",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NovaTextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RoughCutPreset.entries.forEach { preset ->
                        val isSelected = selectedPreset == preset
                        Surface(
                            onClick = { selectedPreset = preset },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) NovaPanelAlt else NovaDarkBg,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) NovaCyan else NovaBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(68.dp)
                                .testTag("preset_${preset.name}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(6.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = preset.title.take(10),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) NovaCyan else NovaTextPrimary
                                )
                                Text(
                                    text = "${preset.targetDurationMs / 1000}s Cut",
                                    fontSize = 9.sp,
                                    color = NovaAmber,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = NovaPanelAlt),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = NovaGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "What AI Smart Cut does:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NovaTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Detects & deletes silence gaps (> 400ms dead air)\n• Protects first 3s Viral Hook retention\n• Aligns snappy cuts to audio beat BPM drops\n• Eliminates boring talking head pauses",
                            fontSize = 10.sp,
                            color = NovaTextSecondary,
                            lineHeight = 14.sp
                        )
                    }
                }

                Button(
                    onClick = { viewModel.runAiAnalysis(selectedPreset) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NovaCyan,
                        contentColor = NovaDarkBg
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("btn_run_ai_analysis")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Analyze & Generate Rough Cut",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        is AiStatus.Analyzing -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = NovaCyan,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = aiStatus.statusMessage,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NovaTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { aiStatus.progress },
                    color = NovaCyan,
                    trackColor = NovaBorder,
                    modifier = Modifier
                        .width(200.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }
        }

        is AiStatus.Success -> {
            val res = aiStatus.result
            val savedMs = (res.totalOriginalDurationMs - res.estimatedTrimmedDurationMs)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Success Badge
                Surface(
                    color = NovaGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NovaGreen)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = NovaGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "AI Analysis Complete! Saved ${String.format("%.1f", savedMs / 1000f)}s dead air",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NovaGreen
                            )
                            Text(
                                text = "Found ${res.silenceGaps.size} silence gaps & ${res.highlights.size} key scenes",
                                fontSize = 10.sp,
                                color = NovaTextSecondary
                            )
                        }
                    }
                }

                // Highlights Summary List
                Text(
                    text = "Detected Key Moments:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NovaTextPrimary
                )

                res.highlights.forEach { hl ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NovaPanelAlt),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = hl.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NovaCyan
                                )
                                Text(
                                    text = "${hl.category} • ${(hl.startMs / 1000f)}s to ${(hl.endMs / 1000f)}s",
                                    fontSize = 9.sp,
                                    color = NovaTextSecondary
                                )
                            }
                            Surface(
                                color = NovaAmber.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Score: ${(hl.score * 100).toInt()}%",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NovaAmber,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Apply Button
                Button(
                    onClick = { viewModel.applyAiRoughCut(res) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NovaGreen,
                        contentColor = NovaDarkBg
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("btn_apply_rough_cut")
                ) {
                    Icon(imageVector = Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Apply AI Rough Cut to Timeline",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        is AiStatus.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = aiStatus.message,
                    fontSize = 12.sp,
                    color = NovaPink
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = { viewModel.runAiAnalysis(selectedPreset) }) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
private fun AiCaptionsTab(
    viewModel: EditorViewModel
) {
    var selectedStyle by remember { mutableStateOf(CaptionStylePreset.HORMOZI) }
    var isGenerating by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Choose Viral Subtitle Style",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NovaTextSecondary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CaptionStylePreset.entries.forEach { style ->
                val isSelected = selectedStyle == style
                Surface(
                    onClick = { selectedStyle = style },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) NovaPanelAlt else NovaDarkBg,
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isSelected) NovaCyan else NovaBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(85.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Surface(
                            color = Color(style.highlightColor),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = style.previewWord,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = style.title,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) NovaCyan else NovaTextPrimary,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = NovaPanelAlt),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "AI Speech-to-Text Features:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NovaTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Animated word-by-word bouncing timing\n• Automatic punchline keyword highlighting\n• Positioned within TikTok safe zones\n• Fully editable in the timeline",
                    fontSize = 10.sp,
                    color = NovaTextSecondary,
                    lineHeight = 14.sp
                )
            }
        }

        Button(
            onClick = {
                isGenerating = true
                viewModel.generateAutoCaptions(selectedStyle)
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NovaCyan,
                contentColor = NovaDarkBg
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .testTag("btn_generate_captions")
        ) {
            Icon(imageVector = Icons.Default.Subtitles, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isGenerating) "Captions Generated on Timeline!" else "Generate AI Captions",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AiCinematicGradeTab(
    viewModel: EditorViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = NovaPanelAlt),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = NovaAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AI Scene Lighting & Tone Harmonizer",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaAmber
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Analyzes camera exposure, dynamic range, and shadows across all video clips. Automatically matches skin tones, applies film tone curve, and selects optimal LUT.",
                    fontSize = 10.sp,
                    color = NovaTextSecondary,
                    lineHeight = 14.sp
                )
            }
        }

        Button(
            onClick = { viewModel.applyAiCinematicGrade() },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NovaAmber,
                contentColor = NovaDarkBg
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .testTag("btn_apply_ai_color")
        ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Apply AI Cinematic Color Grade",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
