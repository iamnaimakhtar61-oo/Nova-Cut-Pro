package com.example

import com.example.data.model.ColorGradingConfig
import com.example.data.model.EasingType
import com.example.data.model.KeyframeItem
import com.example.data.model.LutPreset
import com.example.data.model.RoughCutPreset
import com.example.data.model.TrackType
import com.example.data.repository.AiVideoAssistant
import com.example.data.repository.ProjectRepository
import com.example.ui.viewmodel.EditorViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testKeyframeInterpolation() {
        val viewModel = EditorViewModel()
        val project = ProjectRepository.projects.value.first()
        val mainClip = project.clips.first { it.trackType == TrackType.VIDEO_MAIN }

        // Test at start
        val kfStart = viewModel.interpolateKeyframeAt(mainClip, 0L)
        assertNotNull(kfStart)

        // Test easing interpolation
        val bezierVal = EasingType.BEZIER.interpolate(0.5f)
        assertTrue(bezierVal in 0f..1f)
    }

    @Test
    fun testAiRoughCutAnalysis() = runBlocking {
        val project = ProjectRepository.projects.value.first()
        val result = AiVideoAssistant.analyzeVideo(project.clips, RoughCutPreset.TIKTOK_15S)

        assertNotNull(result)
        assertTrue(result.silenceGaps.isNotEmpty())
        assertTrue(result.highlights.isNotEmpty())
        assertTrue(result.estimatedTrimmedDurationMs <= result.totalOriginalDurationMs)
    }

    @Test
    fun testColorGradingLuts() {
        val config = ColorGradingConfig(lut = LutPreset.CYBERPUNK)
        assertEquals(LutPreset.CYBERPUNK, config.lut)
        assertTrue(LutPreset.entries.size >= 8)
    }
}
