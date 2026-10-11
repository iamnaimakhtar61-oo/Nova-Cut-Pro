package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ColorGradingConfig
import com.example.data.model.LutPreset
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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun ColorGradingPanel(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val selectedClip = viewModel.getSelectedClip() ?: viewModel.getActiveMainVideoClip()
    val grading = selectedClip?.colorGrading ?: ColorGradingConfig()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Primary Sliders", "3-Way Wheels", "LUT Presets", "Color Scopes")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(310.dp)
            .background(NovaPanelBg)
            .padding(top = 8.dp)
    ) {
        // Header with AI Auto-Grade & Reset
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "COLOR GRADING STUDIO",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = NovaCyan
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // AI Cinematic Auto-Grade Button
                Button(
                    onClick = { viewModel.applyAiCinematicGrade() },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NovaCyan,
                        contentColor = NovaDarkBg
                    ),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("btn_ai_auto_grade")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Auto Grade",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AI Auto-Grade",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Reset Button
                IconButton(
                    onClick = { viewModel.resetColorGrading() },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset Color Grade",
                        tint = NovaTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Subtabs: Primary Sliders, 3-Way Wheels, LUTs, Scopes
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

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            when (selectedTab) {
                0 -> PrimarySlidersTab(grading = grading, onUpdate = { viewModel.updateColorGrading(it) })
                1 -> ThreeWayWheelsTab(grading = grading, onUpdate = { viewModel.updateColorGrading(it) })
                2 -> LutPresetsTab(grading = grading, onSelectLut = { viewModel.applyLut(it) })
                3 -> ColorScopesTab(grading = grading)
            }
        }
    }
}

@Composable
private fun PrimarySlidersTab(
    grading: ColorGradingConfig,
    onUpdate: ((ColorGradingConfig) -> ColorGradingConfig) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Temperature (Cool Blue vs Warm Amber)
        GradeSliderRow(
            label = "Temperature",
            value = grading.temperature,
            valueRange = -1.0f..1.0f,
            activeColor = if (grading.temperature > 0) NovaAmber else NovaCyan,
            displayValue = if (grading.temperature > 0) "+${(grading.temperature * 100).toInt()}" else "${(grading.temperature * 100).toInt()}",
            onValueChange = { newVal -> onUpdate { it.copy(temperature = newVal) } }
        )

        // Tint (Green vs Magenta)
        GradeSliderRow(
            label = "Tint (Magenta)",
            value = grading.tint,
            valueRange = -1.0f..1.0f,
            activeColor = NovaPink,
            displayValue = if (grading.tint > 0) "+${(grading.tint * 100).toInt()}" else "${(grading.tint * 100).toInt()}",
            onValueChange = { newVal -> onUpdate { it.copy(tint = newVal) } }
        )

        // Exposure
        GradeSliderRow(
            label = "Exposure",
            value = grading.exposure,
            valueRange = -1.0f..1.0f,
            activeColor = NovaCyan,
            displayValue = if (grading.exposure > 0) "+${(grading.exposure * 100).toInt()}" else "${(grading.exposure * 100).toInt()}",
            onValueChange = { newVal -> onUpdate { it.copy(exposure = newVal) } }
        )

        // Contrast
        GradeSliderRow(
            label = "Contrast",
            value = grading.contrast,
            valueRange = -1.0f..1.0f,
            activeColor = NovaAmber,
            displayValue = if (grading.contrast > 0) "+${(grading.contrast * 100).toInt()}" else "${(grading.contrast * 100).toInt()}",
            onValueChange = { newVal -> onUpdate { it.copy(contrast = newVal) } }
        )

        // Saturation
        GradeSliderRow(
            label = "Saturation",
            value = grading.saturation,
            valueRange = 0.0f..2.0f,
            activeColor = NovaGreen,
            displayValue = "${(grading.saturation * 100).toInt()}%",
            onValueChange = { newVal -> onUpdate { it.copy(saturation = newVal) } }
        )

        // Vignette
        GradeSliderRow(
            label = "Vignette",
            value = grading.vignette,
            valueRange = 0.0f..1.0f,
            activeColor = NovaPurple,
            displayValue = "${(grading.vignette * 100).toInt()}%",
            onValueChange = { newVal -> onUpdate { it.copy(vignette = newVal) } }
        )
    }
}

@Composable
private fun GradeSliderRow(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    activeColor: Color,
    displayValue: String,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = NovaTextPrimary,
            modifier = Modifier.width(95.dp)
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = activeColor,
                activeTrackColor = activeColor,
                inactiveTrackColor = NovaBorder
            ),
            modifier = Modifier
                .weight(1f)
                .height(28.dp)
        )

        Text(
            text = displayValue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = activeColor,
            modifier = Modifier
                .width(42.dp)
                .padding(start = 6.dp)
        )
    }
}

@Composable
private fun ThreeWayWheelsTab(
    grading: ColorGradingConfig,
    onUpdate: ((ColorGradingConfig) -> ColorGradingConfig) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lift (Shadows)
        ColorWheelView(
            title = "LIFT (Shadows)",
            hue = grading.liftHue,
            sat = grading.liftSat,
            onWheelChange = { h, s -> onUpdate { it.copy(liftHue = h, liftSat = s) } }
        )

        // Gamma (Midtones)
        ColorWheelView(
            title = "GAMMA (Midtones)",
            hue = grading.gammaHue,
            sat = grading.gammaSat,
            onWheelChange = { h, s -> onUpdate { it.copy(gammaHue = h, gammaSat = s) } }
        )

        // Gain (Highlights)
        ColorWheelView(
            title = "GAIN (Highlights)",
            hue = grading.gainHue,
            sat = grading.gainSat,
            onWheelChange = { h, s -> onUpdate { it.copy(gainHue = h, gainSat = s) } }
        )
    }
}

@Composable
private fun ColorWheelView(
    title: String,
    hue: Float,
    sat: Float,
    onWheelChange: (Float, Float) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = NovaTextSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Box(
            modifier = Modifier
                .size(90.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val touch = change.position
                        val dx = touch.x - center.x
                        val dy = touch.y - center.y
                        val radius = size.width / 2f

                        val distance = sqrt(dx * dx + dy * dy).coerceAtMost(radius)
                        val newSat = (distance / radius).coerceIn(0f, 1f)
                        val angleRad = atan2(dy, dx)
                        val newHue = ((angleRad * 180f / Math.PI) + 360f) % 360f

                        onWheelChange(newHue.toFloat(), newSat)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = size.width / 2f
                val center = Offset(r, r)

                // Chroma chromatic wheel circle
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Red, Color.Yellow, Color.Green,
                            Color.Cyan, Color.Blue, Color.Magenta, Color.Red
                        )
                    ),
                    radius = r,
                    style = Stroke(width = 6f)
                )

                // Wheel interior shade
                drawCircle(color = Color(0x331F293D), radius = r - 4f)

                // Center crosshairs
                drawLine(Color(0x33FFFFFF), Offset(center.x - 8f, center.y), Offset(center.x + 8f, center.y), 1f)
                drawLine(Color(0x33FFFFFF), Offset(center.x, center.y - 8f), Offset(center.x, center.y + 8f), 1f)

                // Selected point indicator
                val angleRad = Math.toRadians(hue.toDouble())
                val indicatorDist = sat * (r - 8f)
                val indX = (center.x + indicatorDist * cos(angleRad)).toFloat()
                val indY = (center.y + indicatorDist * sin(angleRad)).toFloat()

                drawCircle(color = Color.White, radius = 5f, center = Offset(indX, indY))
                drawCircle(color = NovaCyan, radius = 3f, center = Offset(indX, indY))
            }
        }
    }
}

@Composable
private fun LutPresetsTab(
    grading: ColorGradingConfig,
    onSelectLut: (LutPreset) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        LutPreset.entries.forEach { lut ->
            val isSelected = grading.lut == lut
            Surface(
                onClick = { onSelectLut(lut) },
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) NovaPanelAlt else NovaDarkBg,
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (isSelected) NovaCyan else NovaBorder
                ),
                modifier = Modifier
                    .width(110.dp)
                    .height(180.dp)
                    .testTag("lut_${lut.name}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Preview swatch
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0F172A),
                                        Color(lut.previewTint).copy(alpha = 0.9f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = lut.title.take(3).uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = lut.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) NovaCyan else NovaTextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = lut.description,
                            fontSize = 9.sp,
                            color = NovaTextSecondary,
                            maxLines = 2,
                            lineHeight = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorScopesTab(
    grading: ColorGradingConfig
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = "RGB PARADE / HISTOGRAM SCOPES",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = NovaTextSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Simulated RGB Parade Scope
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF05060A), RoundedCornerShape(6.dp))
                .border(1.dp, NovaBorder, RoundedCornerShape(6.dp))
        ) {
            val w = size.width
            val h = size.height
            val thirdW = w / 3f

            // Baseline grid lines
            for (i in 1..4) {
                val y = h * (i / 5f)
                drawLine(Color(0x1AFFFFFF), Offset(0f, y), Offset(w, y), 1f)
            }

            // RED Channel waveform
            val redGain = (1.0f + grading.temperature * 0.4f).coerceIn(0.2f, 1.8f)
            for (x in 0 until thirdW.toInt() step 3) {
                val waveH = (sin(x * 0.15) * 20f + (h * 0.5f) * redGain).toFloat().coerceIn(10f, h - 10f)
                drawLine(
                    color = Color(0x88FF3333),
                    start = Offset(x.toFloat(), h - waveH),
                    end = Offset(x.toFloat(), h - 5f),
                    strokeWidth = 2f
                )
            }

            // GREEN Channel waveform
            val greenGain = (1.0f - grading.tint * 0.2f).coerceIn(0.2f, 1.8f)
            for (x in 0 until thirdW.toInt() step 3) {
                val waveH = (cos(x * 0.12) * 22f + (h * 0.52f) * greenGain).toFloat().coerceIn(10f, h - 10f)
                drawLine(
                    color = Color(0x8833FF66),
                    start = Offset(thirdW + x, h - waveH),
                    end = Offset(thirdW + x, h - 5f),
                    strokeWidth = 2f
                )
            }

            // BLUE Channel waveform
            val blueGain = (1.0f - grading.temperature * 0.4f).coerceIn(0.2f, 1.8f)
            for (x in 0 until thirdW.toInt() step 3) {
                val waveH = (sin(x * 0.18) * 18f + (h * 0.48f) * blueGain).toFloat().coerceIn(10f, h - 10f)
                drawLine(
                    color = Color(0x883399FF),
                    start = Offset(thirdW * 2 + x, h - waveH),
                    end = Offset(thirdW * 2 + x, h - 5f),
                    strokeWidth = 2f
                )
            }
        }
    }
}
