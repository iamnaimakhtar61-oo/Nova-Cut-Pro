package com.example.data.repository

import com.example.data.model.AspectRatio
import com.example.data.model.AudioTrack
import com.example.data.model.AutoCaptionItem
import com.example.data.model.CaptionStylePreset
import com.example.data.model.ClipItem
import com.example.data.model.ColorGradingConfig
import com.example.data.model.EasingType
import com.example.data.model.EffectItem
import com.example.data.model.EffectType
import com.example.data.model.KeyframeItem
import com.example.data.model.LutPreset
import com.example.data.model.Project
import com.example.data.model.TextOverlayConfig
import com.example.data.model.TrackType
import com.example.data.model.TransitionItem
import com.example.data.model.TransitionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object ProjectRepository {

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    init {
        _projects.value = createSampleProjects()
    }

    fun getProjectById(id: String): Project? {
        return _projects.value.firstOrNull { it.id == id } ?: _projects.value.firstOrNull()
    }

    fun saveProject(project: Project) {
        val current = _projects.value.toMutableList()
        val index = current.indexOfFirst { it.id == project.id }
        val updatedProject = project.copy(
            lastEditedEpochMs = System.currentTimeMillis(),
            isCloudSynced = true,
            cloudSyncTimestamp = "Just now"
        )
        if (index >= 0) {
            current[index] = updatedProject
        } else {
            current.add(0, updatedProject)
        }
        _projects.value = current
    }

    fun createNewProject(
        title: String = "Untitled Social Cut",
        aspectRatio: AspectRatio = AspectRatio.RATIO_9_16
    ): Project {
        val newId = "proj_${UUID.randomUUID().toString().take(8)}"
        val defaultAudio = MediaLibraryRepository.trendingAudioList.first()

        // Create starter clips: 2 video clips on main track, 1 text track, 1 audio track
        val clip1 = ClipItem(
            id = "clip_main_1",
            trackType = TrackType.VIDEO_MAIN,
            name = "A-Roll Street Night",
            startMs = 0L,
            endMs = 6_500L,
            sourceDurationMs = 10_000L,
            visualTheme = "stock_cyber",
            colorGrading = ColorGradingConfig(
                lut = LutPreset.CYBERPUNK,
                contrast = 0.2f,
                saturation = 1.3f
            ),
            keyframes = listOf(
                KeyframeItem("kf_1", timeMs = 0L, scale = 1.0f, posX = 0f, posY = 0f, easing = EasingType.BEZIER),
                KeyframeItem("kf_2", timeMs = 6_000L, scale = 1.15f, posX = 0.05f, posY = -0.05f, easing = EasingType.BEZIER)
            ),
            effects = listOf(
                EffectItem("fx_1", EffectType.RGB_SPLIT, intensity = 0.6f)
            ),
            transition = TransitionItem("tr_1", TransitionType.WHIP_PAN, durationMs = 350L)
        )

        val clip2 = ClipItem(
            id = "clip_main_2",
            trackType = TrackType.VIDEO_MAIN,
            name = "A-Roll Sunset Motion",
            startMs = 6_500L,
            endMs = 14_000L,
            sourceDurationMs = 12_000L,
            visualTheme = "stock_skate",
            colorGrading = ColorGradingConfig(
                lut = LutPreset.GOLDEN_HOUR,
                temperature = 0.3f,
                saturation = 1.2f
            ),
            keyframes = listOf(
                KeyframeItem("kf_3", timeMs = 6_500L, scale = 1.2f, rotation = 0f),
                KeyframeItem("kf_4", timeMs = 13_500L, scale = 1.0f, rotation = -2f)
            )
        )

        val textClip = ClipItem(
            id = "clip_text_1",
            trackType = TrackType.TEXT_GRAPHICS,
            name = "Viral Hormozi Hook",
            startMs = 500L,
            endMs = 6_000L,
            sourceDurationMs = 5_500L,
            textOverlay = TextOverlayConfig(
                text = "STOP SCROLLING ⚡",
                stylePreset = CaptionStylePreset.HORMOZI,
                fontSizeSp = 28f,
                posY = 0.62f
            ),
            captions = listOf(
                AutoCaptionItem("c1", "STOP SCROLLING ⚡", 500L, 2200L, true),
                AutoCaptionItem("c2", "TRY THIS NEW EFFECT", 2300L, 4500L, false),
                AutoCaptionItem("c3", "IN NOVACUT PRO", 4600L, 5900L, true)
            )
        )

        val audioClip = ClipItem(
            id = "clip_audio_bgm",
            trackType = TrackType.AUDIO_BGM,
            name = defaultAudio.title,
            startMs = 0L,
            endMs = 14_000L,
            sourceDurationMs = defaultAudio.durationMs,
            volume = 0.85f,
            audioTrack = defaultAudio
        )

        val project = Project(
            id = newId,
            title = title,
            aspectRatio = aspectRatio,
            fps = 30,
            resolutionLabel = "1080p FHD",
            clips = listOf(clip1, clip2, textClip, audioClip),
            totalDurationMs = 14_000L,
            isCloudSynced = true,
            thumbnailGradientColors = listOf(0xFF0F172A, 0xFF00E5FF)
        )

        val updated = _projects.value.toMutableList()
        updated.add(0, project)
        _projects.value = updated
        return project
    }

    fun duplicateProject(id: String): Project? {
        val original = getProjectById(id) ?: return null
        val copy = original.copy(
            id = "proj_${UUID.randomUUID().toString().take(8)}",
            title = "${original.title} (Copy)",
            lastEditedEpochMs = System.currentTimeMillis(),
            cloudSyncTimestamp = "Just now"
        )
        val updated = _projects.value.toMutableList()
        updated.add(0, copy)
        _projects.value = updated
        return copy
    }

    fun deleteProject(id: String) {
        val updated = _projects.value.filter { it.id != id }
        _projects.value = updated
    }

    private fun createSampleProjects(): List<Project> {
        val audio1 = MediaLibraryRepository.trendingAudioList[0]
        val audio2 = MediaLibraryRepository.trendingAudioList[1]
        val audio3 = MediaLibraryRepository.trendingAudioList[2]

        val project1 = Project(
            id = "proj_viral_tiktok",
            title = "Cyberpunk TikTok Hook 9:16",
            aspectRatio = AspectRatio.RATIO_9_16,
            fps = 60,
            resolutionLabel = "1080p FHD",
            totalDurationMs = 16_000L,
            thumbnailGradientColors = listOf(0xFF090A0F, 0xFF00E5FF, 0xFFFF2A6D),
            isCloudSynced = true,
            cloudSyncTimestamp = "Synced 2m ago",
            clips = listOf(
                ClipItem(
                    id = "c1_v1",
                    trackType = TrackType.VIDEO_MAIN,
                    name = "Neon Streets Intro",
                    startMs = 0L,
                    endMs = 5_000L,
                    sourceDurationMs = 8_000L,
                    visualTheme = "stock_cyber",
                    colorGrading = ColorGradingConfig(lut = LutPreset.CYBERPUNK, contrast = 0.25f, saturation = 1.4f),
                    keyframes = listOf(
                        KeyframeItem("kf_c1_1", 0L, scale = 1.0f, posX = 0f, posY = 0f),
                        KeyframeItem("kf_c1_2", 4_500L, scale = 1.2f, posX = 0.08f, posY = -0.04f)
                    ),
                    effects = listOf(EffectItem("fx_glitch", EffectType.GLITCH, intensity = 0.7f)),
                    transition = TransitionItem("tr_zoom", TransitionType.ZOOM_IN, 300L)
                ),
                ClipItem(
                    id = "c1_v2",
                    trackType = TrackType.VIDEO_MAIN,
                    name = "Golden Skater Drop",
                    startMs = 5_000L,
                    endMs = 11_000L,
                    sourceDurationMs = 10_000L,
                    visualTheme = "stock_skate",
                    colorGrading = ColorGradingConfig(lut = LutPreset.GOLDEN_HOUR, temperature = 0.35f, saturation = 1.2f),
                    keyframes = listOf(
                        KeyframeItem("kf_c2_1", 5_000L, scale = 1.25f),
                        KeyframeItem("kf_c2_2", 10_500L, scale = 1.0f)
                    ),
                    effects = listOf(EffectItem("fx_rgb", EffectType.RGB_SPLIT, intensity = 0.5f)),
                    transition = TransitionItem("tr_whip", TransitionType.WHIP_PAN, 350L)
                ),
                ClipItem(
                    id = "c1_v3",
                    trackType = TrackType.VIDEO_MAIN,
                    name = "Workout Power Punch",
                    startMs = 11_000L,
                    endMs = 16_000L,
                    sourceDurationMs = 7_000L,
                    visualTheme = "stock_gym",
                    colorGrading = ColorGradingConfig(lut = LutPreset.TEAL_ORANGE, contrast = 0.3f, saturation = 1.3f)
                ),
                // PIP B-Roll Track
                ClipItem(
                    id = "c1_pip",
                    trackType = TrackType.VIDEO_PIP,
                    name = "Reaction Face Cam (PIP)",
                    startMs = 2_000L,
                    endMs = 8_000L,
                    sourceDurationMs = 6_000L,
                    visualTheme = "stock_vlog",
                    colorGrading = ColorGradingConfig(lut = LutPreset.CLEAN_VLOG),
                    keyframes = listOf(
                        KeyframeItem("kf_pip_1", 2000L, posX = 0.35f, posY = -0.35f, scale = 0.35f),
                        KeyframeItem("kf_pip_2", 8000L, posX = 0.35f, posY = -0.35f, scale = 0.38f)
                    )
                ),
                // Caption Track
                ClipItem(
                    id = "c1_text",
                    trackType = TrackType.TEXT_GRAPHICS,
                    name = "Auto Subtitles",
                    startMs = 0L,
                    endMs = 16_000L,
                    sourceDurationMs = 16_000L,
                    textOverlay = TextOverlayConfig(
                        text = "WAIT FOR THE DROP 🔥",
                        stylePreset = CaptionStylePreset.HORMOZI,
                        posY = 0.65f
                    ),
                    captions = listOf(
                        AutoCaptionItem("cap_1", "WAIT FOR THE DROP 🔥", 0L, 2500L, true),
                        AutoCaptionItem("cap_2", "THIS IS INSANE", 2600L, 5000L, false),
                        AutoCaptionItem("cap_3", "SMOOTH TRANSITION!", 5100L, 8200L, true),
                        AutoCaptionItem("cap_4", "FOLLOW FOR PART 2", 8300L, 11000L, false),
                        AutoCaptionItem("cap_5", "LINK IN BIO ✨", 11100L, 16000L, true)
                    )
                ),
                // Audio Tracks
                ClipItem(
                    id = "c1_audio",
                    trackType = TrackType.AUDIO_BGM,
                    name = audio1.title,
                    startMs = 0L,
                    endMs = 16_000L,
                    sourceDurationMs = audio1.durationMs,
                    volume = 0.9f,
                    audioTrack = audio1
                ),
                ClipItem(
                    id = "c1_sfx",
                    trackType = TrackType.AUDIO_SFX,
                    name = "Whoosh Drop SFX",
                    startMs = 4_800L,
                    endMs = 5_800L,
                    sourceDurationMs = 1_000L,
                    volume = 1.0f
                )
            )
        )

        val project2 = Project(
            id = "proj_golden_reel",
            title = "Aesthetic Sunset Reel 9:16",
            aspectRatio = AspectRatio.RATIO_9_16,
            fps = 30,
            resolutionLabel = "4K Ultra HD",
            totalDurationMs = 12_000L,
            thumbnailGradientColors = listOf(0xFF2E1065, 0xFFFFB703, 0xFFFF6B35),
            isCloudSynced = true,
            cloudSyncTimestamp = "Synced 1h ago",
            clips = listOf(
                ClipItem(
                    id = "c2_v1",
                    trackType = TrackType.VIDEO_MAIN,
                    name = "Golden Glow Sunset",
                    startMs = 0L,
                    endMs = 12_000L,
                    sourceDurationMs = 15_000L,
                    visualTheme = "stock_skate",
                    colorGrading = ColorGradingConfig(lut = LutPreset.GOLDEN_HOUR, temperature = 0.45f, saturation = 1.25f),
                    keyframes = listOf(
                        KeyframeItem("kf_s1", 0L, scale = 1.0f),
                        KeyframeItem("kf_s2", 11_500L, scale = 1.18f)
                    )
                ),
                ClipItem(
                    id = "c2_audio",
                    trackType = TrackType.AUDIO_BGM,
                    name = audio3.title,
                    startMs = 0L,
                    endMs = 12_000L,
                    sourceDurationMs = audio3.durationMs,
                    volume = 0.8f,
                    audioTrack = audio3
                ),
                ClipItem(
                    id = "c2_text",
                    trackType = TrackType.TEXT_GRAPHICS,
                    name = "Minimalist Subtitles",
                    startMs = 0L,
                    endMs = 12_000L,
                    sourceDurationMs = 12_000L,
                    textOverlay = TextOverlayConfig(
                        text = "Golden Hour Magic ✨",
                        stylePreset = CaptionStylePreset.MINIMAL_PILL,
                        posY = 0.68f
                    ),
                    captions = listOf(
                        AutoCaptionItem("c2_c1", "golden hour magic ✨", 0L, 3500L, false),
                        AutoCaptionItem("c2_c2", "nothing beats this light", 3600L, 7800L, true),
                        AutoCaptionItem("c2_c3", "shot on 35mm film", 7900L, 12000L, false)
                    )
                )
            )
        )

        val project3 = Project(
            id = "proj_tech_unboxing",
            title = "Titanium Phone Unboxing 16:9",
            aspectRatio = AspectRatio.RATIO_16_9,
            fps = 60,
            resolutionLabel = "4K Ultra HD",
            totalDurationMs = 15_000L,
            thumbnailGradientColors = listOf(0xFF18181B, 0xFF38BDF8, 0xFFE2E8F0),
            isCloudSynced = true,
            cloudSyncTimestamp = "Synced yesterday",
            clips = listOf(
                ClipItem(
                    id = "c3_v1",
                    trackType = TrackType.VIDEO_MAIN,
                    name = "Clean Studio Macro",
                    startMs = 0L,
                    endMs = 15_000L,
                    sourceDurationMs = 20_000L,
                    visualTheme = "stock_tech",
                    colorGrading = ColorGradingConfig(lut = LutPreset.CLEAN_VLOG, contrast = 0.15f, exposure = 0.05f)
                ),
                ClipItem(
                    id = "c3_audio",
                    trackType = TrackType.AUDIO_BGM,
                    name = audio2.title,
                    startMs = 0L,
                    endMs = 15_000L,
                    sourceDurationMs = audio2.durationMs,
                    volume = 0.75f,
                    audioTrack = audio2
                )
            )
        )

        return listOf(project1, project2, project3)
    }
}
