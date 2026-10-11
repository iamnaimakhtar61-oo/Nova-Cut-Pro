package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AspectRatio
import com.example.data.model.Project
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
fun ProjectListScreen(
    viewModel: EditorViewModel,
    onOpenProject: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.allProjects.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var newProjectTitle by remember { mutableStateOf("") }
    var selectedRatio by remember { mutableStateOf(AspectRatio.RATIO_9_16) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(NovaDarkBg)
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = NovaDarkBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = NovaCyan,
                contentColor = NovaDarkBg,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .shadow(12.dp, CircleShape)
                    .testTag("fab_new_project")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Project",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NovaCut",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = NovaCyan,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = " PRO",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = NovaPink,
                                letterSpacing = (-0.5).sp
                            )
                        }
                        Text(
                            text = "Professional Social Video Studio",
                            fontSize = 11.sp,
                            color = NovaTextSecondary
                        )
                    }

                    // Cloud Sync Pill
                    Surface(
                        color = NovaPanelAlt,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = "Cloud Synced",
                                tint = NovaGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "NovaCloud Sync",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NovaTextPrimary
                            )
                        }
                    }
                }
            }

            // Quick Social Templates Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "START FROM SOCIAL ASPECT RATIO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaTextSecondary,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AspectRatio.entries.forEach { ratio ->
                            Surface(
                                onClick = {
                                    viewModel.createProject(
                                        title = "${ratio.platformHint.take(12)} Project",
                                        aspectRatio = ratio
                                    )
                                    viewModel.currentProject.value?.let { onOpenProject(it.id) }
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = NovaPanelBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(78.dp)
                                    .testTag("ratio_template_${ratio.name}")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Surface(
                                        color = NovaCyan.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = ratio.label,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = NovaCyan
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = ratio.platformHint.take(8),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NovaTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // AI Features Banner
            item {
                Surface(
                    onClick = { showCreateDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    color = NovaPanelBg,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, NovaCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = NovaCyan.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = NovaCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AI One-Tap Social Assembly",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NovaCyan
                            )
                            Text(
                                text = "Auto silence trimming, scene hook detection & Hormozi viral captions",
                                fontSize = 10.sp,
                                color = NovaTextSecondary
                            )
                        }
                    }
                }
            }

            // Recent Projects Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT PROJECTS (${projects.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaTextSecondary,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Projects List
            items(projects) { project ->
                ProjectCardItem(
                    project = project,
                    onOpen = {
                        viewModel.loadProject(project.id)
                        onOpenProject(project.id)
                    },
                    onDuplicate = { viewModel.duplicateCurrentProject() },
                    onDelete = { viewModel.deleteProject(project.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    // Create New Project Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = NovaPanelBg,
            title = {
                Text(
                    text = "Create New Social Video",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NovaTextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newProjectTitle,
                        onValueChange = { newProjectTitle = it },
                        label = { Text("Project Title") },
                        placeholder = { Text("e.g. Viral TikTok Skit") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NovaCyan,
                            unfocusedBorderColor = NovaBorder,
                            focusedTextColor = NovaTextPrimary,
                            unfocusedTextColor = NovaTextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_project_title")
                    )

                    Text("Aspect Ratio", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NovaTextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AspectRatio.entries.forEach { ratio ->
                            val isSel = selectedRatio == ratio
                            Surface(
                                onClick = { selectedRatio = ratio },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) NovaCyan else NovaPanelAlt,
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = ratio.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) NovaDarkBg else NovaTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val title = newProjectTitle.ifBlank { "Social Reel Cut" }
                        viewModel.createProject(title, selectedRatio)
                        showCreateDialog = false
                        viewModel.currentProject.value?.let { onOpenProject(it.id) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NovaCyan, contentColor = NovaDarkBg),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_confirm_create_project")
                ) {
                    Text("Create & Open", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = NovaTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun ProjectCardItem(
    project: Project,
    onOpen: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(
        onClick = onOpen,
        shape = RoundedCornerShape(12.dp),
        color = NovaPanelBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_card_${project.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail with gradient colors & play emblem
            Box(
                modifier = Modifier
                    .size(width = 68.dp, height = 78.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(
                            colors = project.thumbnailGradientColors.map { Color(it) }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = Color(0x66000000),
                    shape = CircleShape,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Surface(
                    color = Color(0x99000000),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                ) {
                    Text(
                        text = project.aspectRatio.label,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaCyan,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Project Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NovaTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${project.resolutionLabel} • ${project.fps}fps • ${project.clips.size} clips",
                    fontSize = 10.sp,
                    color = NovaTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = NovaPanelAlt,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${project.totalDurationMs / 1000}s",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NovaGreen,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = project.cloudSyncTimestamp,
                        fontSize = 9.sp,
                        color = NovaTextSecondary
                    )
                }
            }

            // Options button
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Options",
                        tint = NovaTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(NovaPanelAlt)
                ) {
                    DropdownMenuItem(
                        text = { Text("Duplicate Project", color = NovaTextPrimary, fontSize = 12.sp) },
                        onClick = {
                            onDuplicate()
                            menuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete Project", color = NovaPink, fontSize = 12.sp) },
                        onClick = {
                            onDelete()
                            menuExpanded = false
                        }
                    )
                }
            }
        }
    }
}
