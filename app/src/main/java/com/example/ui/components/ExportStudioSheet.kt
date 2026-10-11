package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.viewmodel.ExportConfig
import com.example.ui.viewmodel.ExportStatus

@Composable
fun ExportStudioSheet(
    viewModel: EditorViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val exportStatus by viewModel.exportStatus.collectAsState()
    val exportConfig by viewModel.exportConfig.collectAsState()

    var selectedRes by remember { mutableStateOf(exportConfig.resolution) }
    var selectedFps by remember { mutableIntStateOf(exportConfig.fps) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp)
            .background(NovaPanelBg)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VideoFile,
                    contentDescription = null,
                    tint = NovaCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "EXPORT & RENDER STUDIO",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NovaTextPrimary,
                    letterSpacing = 1.sp
                )
            }

            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = NovaTextSecondary, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (exportStatus) {
            is ExportStatus.Idle -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Resolution choices
                    Text("Select Resolution", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NovaTextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("720p HD", "1080p FHD", "4K Ultra HD").forEach { res ->
                            val isSel = selectedRes.startsWith(res.take(4))
                            Surface(
                                onClick = { selectedRes = res },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) NovaPanelAlt else NovaDarkBg,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSel) NovaCyan else NovaBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = res,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) NovaCyan else NovaTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    // FPS choices
                    Text("Frame Rate", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NovaTextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(24 to "24 fps Cinema", 30 to "30 fps Standard", 60 to "60 fps Smooth").forEach { (fps, label) ->
                            val isSel = selectedFps == fps
                            Surface(
                                onClick = { selectedFps = fps },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) NovaPanelAlt else NovaDarkBg,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSel) NovaCyan else NovaBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) NovaCyan else NovaTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Quality estimate card
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
                            Column {
                                Text("Estimated File Size: ~28.4 MB", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NovaTextPrimary)
                                Text("Format: MP4 (H.264/AAC) • Optimized for TikTok / Reels upload", fontSize = 9.sp, color = NovaTextSecondary)
                            }
                            Surface(
                                color = NovaGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("READY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NovaGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f, fill = false))

                    Button(
                        onClick = {
                            viewModel.updateExportConfig(
                                ExportConfig(resolution = selectedRes, fps = selectedFps)
                            )
                            viewModel.startExport()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NovaCyan, contentColor = NovaDarkBg),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_start_render")
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Render & Export Video", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            is ExportStatus.Rendering -> {
                val rend = exportStatus as ExportStatus.Rendering
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        progress = { rend.progress },
                        color = NovaCyan,
                        trackColor = NovaBorder,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Rendering Frame ${rend.currentFrame} / ${rend.totalFrames}...",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaTextPrimary
                    )
                    Text(
                        text = "${(rend.progress * 100).toInt()}% • Applying Color Shaders & Motion Curves",
                        fontSize = 11.sp,
                        color = NovaTextSecondary
                    )
                }
            }

            is ExportStatus.Finished -> {
                val fin = exportStatus as ExportStatus.Finished
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = NovaGreen, modifier = Modifier.size(48.dp))
                    Text(
                        text = "Export Completed Successfully!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaGreen
                    )
                    Text(
                        text = "${fin.fileName} (${String.format("%.1f", fin.fileSizeBytes / 1_000_000f)} MB in ${fin.renderTimeSeconds}s)",
                        fontSize = 11.sp,
                        color = NovaTextSecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Social Share targets
                    Text("Instant Social Share:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NovaTextPrimary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = NovaPink),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("TikTok", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = NovaPurple),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Instagram", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = NovaAmber),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Shorts", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save to Gallery & Return", color = NovaTextPrimary)
                    }
                }
            }
        }
    }
}
