package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EffectType
import com.example.data.model.TransitionType
import com.example.data.repository.MediaLibraryRepository
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
import com.example.ui.viewmodel.EditorViewModel

@Composable
fun EffectsTransitionsSheet(
    viewModel: EditorViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Video Effects", "Transitions", "Active on Clip")

    val selectedClip = viewModel.getSelectedClip() ?: viewModel.getActiveMainVideoClip()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
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
                    imageVector = Icons.Default.AutoFixHigh,
                    contentDescription = null,
                    tint = NovaPink,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "EFFECTS & TRANSITIONS RACK",
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
            contentColor = NovaPink,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NovaPink,
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
                            color = if (selectedTab == index) NovaPink else NovaTextSecondary
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
                0 -> EffectsTab(onApplyEffect = { viewModel.addEffectToSelectedClip(it) })
                1 -> TransitionsTab(onApplyTransition = { viewModel.setTransition(it) })
                2 -> ActiveRackTab(
                    selectedClip = selectedClip,
                    onRemoveEffect = { viewModel.removeEffect(it) },
                    onRemoveTransition = { viewModel.setTransition(null) }
                )
            }
        }
    }
}

@Composable
private fun EffectsTab(
    onApplyEffect: (EffectType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EffectType.entries.forEach { fx ->
            Surface(
                onClick = { onApplyEffect(fx) },
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
                            color = NovaPink.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MovieFilter,
                                    contentDescription = null,
                                    tint = NovaPink,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = fx.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NovaTextPrimary
                            )
                            Text(
                                text = fx.category,
                                fontSize = 9.sp,
                                color = NovaTextSecondary
                            )
                        }
                    }

                    Text(
                        text = "+ Add to Clip",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaPink
                    )
                }
            }
        }
    }
}

@Composable
private fun TransitionsTab(
    onApplyTransition: (TransitionType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TransitionType.entries.forEach { tr ->
            Surface(
                onClick = { onApplyTransition(tr) },
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
                            color = NovaOrange.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Transform,
                                    contentDescription = null,
                                    tint = NovaOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = tr.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NovaTextPrimary
                        )
                    }

                    Text(
                        text = "Apply Cut",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaOrange
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveRackTab(
    selectedClip: com.example.data.model.ClipItem?,
    onRemoveEffect: (String) -> Unit,
    onRemoveTransition: () -> Unit
) {
    if (selectedClip == null) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text("Select a clip to view applied effects", color = NovaTextSecondary, fontSize = 12.sp)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Active on '${selectedClip.name}':",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NovaCyan
        )

        // Applied Transition
        if (selectedClip.transition != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NovaPanelAlt,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transition: ${selectedClip.transition.type.label} (${selectedClip.transition.durationMs}ms)",
                        fontSize = 11.sp,
                        color = NovaOrange,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onRemoveTransition, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = NovaPink, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Applied Effects
        selectedClip.effects.forEach { fx ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NovaPanelAlt,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Effect: ${fx.type.label}",
                        fontSize = 11.sp,
                        color = NovaPink,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { onRemoveEffect(fx.id) }, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = NovaPink, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        if (selectedClip.effects.isEmpty() && selectedClip.transition == null) {
            Text(
                text = "No effects or transitions applied yet. Browse tabs to add!",
                fontSize = 11.sp,
                color = NovaTextSecondary
            )
        }
    }
}
