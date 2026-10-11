package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AiAnalysisResult
import com.example.data.model.AspectRatio
import com.example.data.model.AudioTrack
import com.example.data.model.CaptionStylePreset
import com.example.data.model.ClipItem
import com.example.data.model.ColorGradingConfig
import com.example.data.model.EasingType
import com.example.data.model.EffectItem
import com.example.data.model.EffectType
import com.example.data.model.KeyframeItem
import com.example.data.model.LutPreset
import com.example.data.model.Project
import com.example.data.model.RoughCutPreset
import com.example.data.model.TextOverlayConfig
import com.example.data.model.TrackType
import com.example.data.model.TransitionItem
import com.example.data.model.TransitionType
import com.example.data.repository.AiVideoAssistant
import com.example.data.repository.MediaLibraryRepository
import com.example.data.repository.ProjectRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

enum class EditorPanel {
    NONE,
    COLOR_GRADING,
    KEYFRAMES,
    EFFECTS_TRANSITIONS,
    AUDIO_MIXER,
    AI_STUDIO,
    EXPORT_RENDER,
    CLIP_EDIT
}

sealed class AiStatus {
    object Idle : AiStatus()
    data class Analyzing(val progress: Float, val statusMessage: String) : AiStatus()
    data class Success(val result: AiAnalysisResult) : AiStatus()
    data class Error(val message: String) : AiStatus()
}

data class ExportConfig(
    val resolution: String = "1080p FHD (1080x1920)",
    val fps: Int = 30,
    val bitrateMbps: Float = 12.0f,
    val format: String = "MP4 (H.264)"
)

sealed class ExportStatus {
    object Idle : ExportStatus()
    data class Rendering(val progress: Float, val currentFrame: Int, val totalFrames: Int) : ExportStatus()
    data class Finished(val fileSizeBytes: Long, val renderTimeSeconds: Float, val fileName: String) : ExportStatus()
}

class EditorViewModel : ViewModel() {

    val allProjects: StateFlow<List<Project>> = ProjectRepository.projects

    private val _currentProject = MutableStateFlow<Project?>(null)
    val currentProject: StateFlow<Project?> = _currentProject.asStateFlow()

    private val _playheadMs = MutableStateFlow(0L)
    val playheadMs: StateFlow<Long> = _playheadMs.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _selectedClipId = MutableStateFlow<String?>(null)
    val selectedClipId: StateFlow<String?> = _selectedClipId.asStateFlow()

    private val _activePanel = MutableStateFlow(EditorPanel.NONE)
    val activePanel: StateFlow<EditorPanel> = _activePanel.asStateFlow()

    private val _timelineZoom = MutableStateFlow(1.0f) // 0.6f to 2.5f
    val timelineZoom: StateFlow<Float> = _timelineZoom.asStateFlow()

    private val _showSafeZoneOverlay = MutableStateFlow(false)
    val showSafeZoneOverlay: StateFlow<Boolean> = _showSafeZoneOverlay.asStateFlow()

    private val _beforeAfterSplit = MutableStateFlow(1.0f) // 1.0 = 100% graded, 0.5 = 50% split
    val beforeAfterSplit: StateFlow<Float> = _beforeAfterSplit.asStateFlow()

    private val _aiStatus = MutableStateFlow<AiStatus>(AiStatus.Idle)
    val aiStatus: StateFlow<AiStatus> = _aiStatus.asStateFlow()

    private val _exportStatus = MutableStateFlow<ExportStatus>(ExportStatus.Idle)
    val exportStatus: StateFlow<ExportStatus> = _exportStatus.asStateFlow()

    private val _exportConfig = MutableStateFlow(ExportConfig())
    val exportConfig: StateFlow<ExportConfig> = _exportConfig.asStateFlow()

    private var playbackJob: Job? = null

    init {
        // Load initial project
        val firstProj = ProjectRepository.projects.value.firstOrNull()
        if (firstProj != null) {
            loadProject(firstProj.id)
        }
    }

    fun loadProject(id: String) {
        val proj = ProjectRepository.getProjectById(id)
        _currentProject.value = proj
        _playheadMs.value = 0L
        _isPlaying.value = false
        playbackJob?.cancel()
        _selectedClipId.value = proj?.clips?.firstOrNull { it.trackType == TrackType.VIDEO_MAIN }?.id
        _activePanel.value = EditorPanel.NONE
    }

    fun createProject(title: String, aspectRatio: AspectRatio) {
        val proj = ProjectRepository.createNewProject(title, aspectRatio)
        loadProject(proj.id)
    }

    fun duplicateCurrentProject() {
        val current = _currentProject.value ?: return
        val duplicate = ProjectRepository.duplicateProject(current.id)
        if (duplicate != null) {
            loadProject(duplicate.id)
        }
    }

    fun deleteProject(id: String) {
        ProjectRepository.deleteProject(id)
        val remaining = ProjectRepository.projects.value
        if (remaining.isNotEmpty()) {
            loadProject(remaining.first().id)
        } else {
            _currentProject.value = null
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        val project = _currentProject.value ?: return
        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val stepMs = 33L // ~30 fps tick
            while (isActive && _isPlaying.value) {
                delay(stepMs)
                val next = _playheadMs.value + stepMs
                if (next >= project.totalDurationMs) {
                    _playheadMs.value = 0L // loop
                } else {
                    _playheadMs.value = next
                }
            }
        }
    }

    fun pausePlayback() {
        _isPlaying.value = false
        playbackJob?.cancel()
    }

    fun seekTo(timeMs: Long) {
        val maxDuration = _currentProject.value?.totalDurationMs ?: 18_000L
        _playheadMs.value = timeMs.coerceIn(0L, maxDuration)
    }

    fun stepFrame(forward: Boolean) {
        val frameMs = 33L
        val maxDuration = _currentProject.value?.totalDurationMs ?: 18_000L
        val delta = if (forward) frameMs else -frameMs
        _playheadMs.value = (_playheadMs.value + delta).coerceIn(0L, maxDuration)
    }

    fun jumpToNextCut() {
        val cuts = getCutPoints().filter { it > _playheadMs.value + 100L }
        val next = cuts.minOrNull()
        if (next != null) {
            seekTo(next)
        }
    }

    fun jumpToPrevCut() {
        val cuts = getCutPoints().filter { it < _playheadMs.value - 100L }
        val prev = cuts.maxOrNull()
        if (prev != null) {
            seekTo(prev)
        } else {
            seekTo(0L)
        }
    }

    private fun getCutPoints(): List<Long> {
        val project = _currentProject.value ?: return emptyList()
        val cuts = mutableSetOf<Long>()
        project.clips.forEach {
            cuts.add(it.startMs)
            cuts.add(it.endMs)
        }
        return cuts.toList().sorted()
    }

    fun selectClip(clipId: String?) {
        _selectedClipId.value = clipId
    }

    fun setPanel(panel: EditorPanel) {
        _activePanel.value = if (_activePanel.value == panel) EditorPanel.NONE else panel
    }

    fun setTimelineZoom(zoom: Float) {
        _timelineZoom.value = zoom.coerceIn(0.5f, 2.5f)
    }

    fun toggleSafeZoneOverlay() {
        _showSafeZoneOverlay.value = !_showSafeZoneOverlay.value
    }

    fun setBeforeAfterSplit(split: Float) {
        _beforeAfterSplit.value = split.coerceIn(0f, 1f)
    }

    // ================= Timeline Clip Operations =================

    fun splitClipAtPlayhead() {
        val project = _currentProject.value ?: return
        val currentPlayhead = _playheadMs.value
        val targetClip = project.clips.firstOrNull { it.id == _selectedClipId.value }
            ?: project.clips.firstOrNull { it.trackType == TrackType.VIDEO_MAIN && currentPlayhead > it.startMs && currentPlayhead < it.endMs }
            ?: return

        if (currentPlayhead <= targetClip.startMs + 200L || currentPlayhead >= targetClip.endMs - 200L) {
            return // too close to boundary
        }

        val firstPart = targetClip.copy(
            endMs = currentPlayhead,
            trimEndMs = targetClip.trimStartMs + (currentPlayhead - targetClip.startMs)
        )

        val secondPart = targetClip.copy(
            id = "clip_${UUID.randomUUID().toString().take(6)}",
            name = "${targetClip.name} (Split)",
            startMs = currentPlayhead,
            trimStartMs = firstPart.trimEndMs
        )

        val updatedClips = project.clips.toMutableList()
        val idx = updatedClips.indexOfFirst { it.id == targetClip.id }
        if (idx >= 0) {
            updatedClips[idx] = firstPart
            updatedClips.add(idx + 1, secondPart)
            updateProject(project.copy(clips = updatedClips))
            _selectedClipId.value = secondPart.id
        }
    }

    fun deleteSelectedClip() {
        val project = _currentProject.value ?: return
        val selectedId = _selectedClipId.value ?: return
        val updated = project.clips.filter { it.id != selectedId }
        val newDuration = updated.maxOfOrNull { it.endMs } ?: 5_000L
        updateProject(project.copy(clips = updated, totalDurationMs = newDuration))
        _selectedClipId.value = updated.firstOrNull()?.id
    }

    fun duplicateSelectedClip() {
        val project = _currentProject.value ?: return
        val selected = project.clips.firstOrNull { it.id == _selectedClipId.value } ?: return
        val duration = selected.durationMs
        val newStart = selected.endMs
        val duplicate = selected.copy(
            id = "clip_${UUID.randomUUID().toString().take(6)}",
            name = "${selected.name} (Copy)",
            startMs = newStart,
            endMs = newStart + duration
        )
        val updated = project.clips + duplicate
        val newDuration = maxOf(project.totalDurationMs, duplicate.endMs)
        updateProject(project.copy(clips = updated, totalDurationMs = newDuration))
        _selectedClipId.value = duplicate.id
    }

    fun updateClipSpeed(clipId: String, speed: Float) {
        val project = _currentProject.value ?: return
        val updated = project.clips.map { clip ->
            if (clip.id == clipId) {
                val newSpeed = speed.coerceIn(0.2f, 8.0f)
                val newDuration = (clip.sourceDurationMs / newSpeed).toLong()
                clip.copy(speed = newSpeed, endMs = clip.startMs + newDuration)
            } else clip
        }
        val maxDur = updated.maxOfOrNull { it.endMs } ?: project.totalDurationMs
        updateProject(project.copy(clips = updated, totalDurationMs = maxDur))
    }

    fun updateClipVolume(clipId: String, volume: Float) {
        val project = _currentProject.value ?: return
        val updated = project.clips.map { clip ->
            if (clip.id == clipId) clip.copy(volume = volume.coerceIn(0f, 2f)) else clip
        }
        updateProject(project.copy(clips = updated))
    }

    // ================= Keyframe Engine =================

    fun addOrToggleKeyframeAtPlayhead() {
        val project = _currentProject.value ?: return
        val clip = project.clips.firstOrNull { it.id == _selectedClipId.value } ?: return
        val currentPlayhead = _playheadMs.value

        val existingKf = clip.keyframes.firstOrNull { Math.abs(it.timeMs - currentPlayhead) < 150L }
        val updatedKeyframes = if (existingKf != null) {
            clip.keyframes.filter { it.id != existingKf.id }
        } else {
            val interpolated = interpolateKeyframeAt(clip, currentPlayhead)
            val newKf = KeyframeItem(
                id = "kf_${UUID.randomUUID().toString().take(6)}",
                timeMs = currentPlayhead,
                posX = interpolated.posX,
                posY = interpolated.posY,
                scale = interpolated.scale,
                rotation = interpolated.rotation,
                opacity = interpolated.opacity,
                easing = EasingType.BEZIER
            )
            (clip.keyframes + newKf).sortedBy { it.timeMs }
        }

        val updatedClips = project.clips.map {
            if (it.id == clip.id) it.copy(keyframes = updatedKeyframes) else it
        }
        updateProject(project.copy(clips = updatedClips))
    }

    fun updateKeyframeValues(
        posX: Float? = null,
        posY: Float? = null,
        scale: Float? = null,
        rotation: Float? = null,
        opacity: Float? = null,
        easing: EasingType? = null
    ) {
        val project = _currentProject.value ?: return
        val clip = project.clips.firstOrNull { it.id == _selectedClipId.value } ?: return
        val currentPlayhead = _playheadMs.value

        var kf = clip.keyframes.minByOrNull { Math.abs(it.timeMs - currentPlayhead) }
        val updatedKeyframes = if (kf != null && Math.abs(kf.timeMs - currentPlayhead) < 400L) {
            clip.keyframes.map {
                if (it.id == kf.id) {
                    it.copy(
                        posX = posX ?: it.posX,
                        posY = posY ?: it.posY,
                        scale = scale ?: it.scale,
                        rotation = rotation ?: it.rotation,
                        opacity = opacity ?: it.opacity,
                        easing = easing ?: it.easing
                    )
                } else it
            }
        } else {
            val base = interpolateKeyframeAt(clip, currentPlayhead)
            val newKf = KeyframeItem(
                id = "kf_${UUID.randomUUID().toString().take(6)}",
                timeMs = currentPlayhead,
                posX = posX ?: base.posX,
                posY = posY ?: base.posY,
                scale = scale ?: base.scale,
                rotation = rotation ?: base.rotation,
                opacity = opacity ?: base.opacity,
                easing = easing ?: EasingType.BEZIER
            )
            (clip.keyframes + newKf).sortedBy { it.timeMs }
        }

        val updatedClips = project.clips.map {
            if (it.id == clip.id) it.copy(keyframes = updatedKeyframes) else it
        }
        updateProject(project.copy(clips = updatedClips))
    }

    // ================= Color Grading =================

    fun updateColorGrading(updateBlock: (ColorGradingConfig) -> ColorGradingConfig) {
        val project = _currentProject.value ?: return
        val clipId = _selectedClipId.value
        val updatedClips = project.clips.map { clip ->
            if (clip.id == clipId || (clipId == null && clip.trackType == TrackType.VIDEO_MAIN)) {
                clip.copy(colorGrading = updateBlock(clip.colorGrading))
            } else clip
        }
        updateProject(project.copy(clips = updatedClips))
    }

    fun applyLut(lutPreset: LutPreset) {
        updateColorGrading { it.copy(lut = lutPreset) }
    }

    fun resetColorGrading() {
        updateColorGrading { ColorGradingConfig() }
    }

    fun applyAiCinematicGrade() {
        val selectedClip = getSelectedClip() ?: return
        val autoGrading = AiVideoAssistant.generateAiCinematicGrading(selectedClip.visualTheme)
        updateColorGrading { autoGrading }
    }

    // ================= Effects & Transitions =================

    fun addEffectToSelectedClip(effectType: EffectType) {
        val project = _currentProject.value ?: return
        val clipId = _selectedClipId.value ?: return
        val newEffect = EffectItem(
            id = "fx_${UUID.randomUUID().toString().take(6)}",
            type = effectType,
            intensity = 0.75f
        )
        val updated = project.clips.map { clip ->
            if (clip.id == clipId) {
                clip.copy(effects = clip.effects + newEffect)
            } else clip
        }
        updateProject(project.copy(clips = updated))
    }

    fun removeEffect(effectId: String) {
        val project = _currentProject.value ?: return
        val clipId = _selectedClipId.value ?: return
        val updated = project.clips.map { clip ->
            if (clip.id == clipId) {
                clip.copy(effects = clip.effects.filter { it.id != effectId })
            } else clip
        }
        updateProject(project.copy(clips = updated))
    }

    fun setTransition(transitionType: TransitionType?) {
        val project = _currentProject.value ?: return
        val clipId = _selectedClipId.value ?: return
        val tr = transitionType?.let { TransitionItem("tr_${UUID.randomUUID().toString().take(6)}", it, 350L) }
        val updated = project.clips.map { clip ->
            if (clip.id == clipId) clip.copy(transition = tr) else clip
        }
        updateProject(project.copy(clips = updated))
    }

    // ================= AI Features: Auto-Rough Cut & Captions =================

    fun runAiAnalysis(preset: RoughCutPreset = RoughCutPreset.TIKTOK_15S) {
        val project = _currentProject.value ?: return
        _aiStatus.value = AiStatus.Analyzing(0.2f, "Detecting silence & speech pauses...")

        viewModelScope.launch {
            try {
                delay(300)
                _aiStatus.value = AiStatus.Analyzing(0.55f, "Identifying viral hook & action scenes...")
                delay(350)
                _aiStatus.value = AiStatus.Analyzing(0.85f, "Aligning cuts to audio BPM beat drops...")
                val result = AiVideoAssistant.analyzeVideo(project.clips, preset)
                _aiStatus.value = AiStatus.Success(result)
            } catch (e: Exception) {
                _aiStatus.value = AiStatus.Error("AI Analysis failed: ${e.message}")
            }
        }
    }

    fun applyAiRoughCut(result: AiAnalysisResult) {
        val project = _currentProject.value ?: return
        val trimmedClips = AiVideoAssistant.assembleRoughCutClips(project.clips, result)
        val newDuration = trimmedClips.maxOfOrNull { it.endMs } ?: 15_000L
        updateProject(
            project.copy(
                clips = trimmedClips,
                totalDurationMs = newDuration
            )
        )
        _selectedClipId.value = trimmedClips.firstOrNull { it.trackType == TrackType.VIDEO_MAIN }?.id
        _playheadMs.value = 0L
        _aiStatus.value = AiStatus.Idle
        _activePanel.value = EditorPanel.NONE
    }

    fun generateAutoCaptions(stylePreset: CaptionStylePreset) {
        val project = _currentProject.value ?: return
        viewModelScope.launch {
            val captions = AiVideoAssistant.generateAutoCaptions(project.totalDurationMs, stylePreset)
            val textClip = ClipItem(
                id = "clip_captions_${UUID.randomUUID().toString().take(6)}",
                trackType = TrackType.TEXT_GRAPHICS,
                name = "AI ${stylePreset.title} Captions",
                startMs = 0L,
                endMs = project.totalDurationMs,
                sourceDurationMs = project.totalDurationMs,
                textOverlay = TextOverlayConfig(
                    text = captions.firstOrNull()?.text ?: "VIRAL CAPTIONS",
                    stylePreset = stylePreset,
                    posY = 0.65f
                ),
                captions = captions
            )
            // Replace or add text track
            val updated = project.clips.filter { it.trackType != TrackType.TEXT_GRAPHICS } + textClip
            updateProject(project.copy(clips = updated))
        }
    }

    // ================= Audio Mixing =================

    fun addTrendingAudio(audio: AudioTrack) {
        val project = _currentProject.value ?: return
        val audioClip = ClipItem(
            id = "audio_${UUID.randomUUID().toString().take(6)}",
            trackType = TrackType.AUDIO_BGM,
            name = audio.title,
            startMs = 0L,
            endMs = project.totalDurationMs,
            sourceDurationMs = audio.durationMs,
            volume = 0.85f,
            audioTrack = audio
        )
        val updated = project.clips.filter { it.trackType != TrackType.AUDIO_BGM } + audioClip
        updateProject(project.copy(clips = updated))
    }

    fun addSoundEffect(name: String) {
        val project = _currentProject.value ?: return
        val currentPlayhead = _playheadMs.value
        val sfxClip = ClipItem(
            id = "sfx_${UUID.randomUUID().toString().take(6)}",
            trackType = TrackType.AUDIO_SFX,
            name = name,
            startMs = currentPlayhead,
            endMs = (currentPlayhead + 1_200L).coerceAtMost(project.totalDurationMs),
            sourceDurationMs = 1_200L,
            volume = 1.0f
        )
        updateProject(project.copy(clips = project.clips + sfxClip))
    }

    fun toggleAudioDucking() {
        val project = _currentProject.value ?: return
        updateProject(project.copy(audioDuckingEnabled = !project.audioDuckingEnabled))
    }

    // ================= Export & Render =================

    fun updateExportConfig(config: ExportConfig) {
        _exportConfig.value = config
    }

    fun startExport() {
        _exportStatus.value = ExportStatus.Rendering(0.0f, 0, 360)
        viewModelScope.launch {
            val totalFrames = 360
            for (frame in 1..totalFrames) {
                delay(12) // simulate GPU render pipeline
                if (frame % 8 == 0 || frame == totalFrames) {
                    val progress = frame / totalFrames.toFloat()
                    _exportStatus.value = ExportStatus.Rendering(progress, frame, totalFrames)
                }
            }
            val projTitle = _currentProject.value?.title?.replace(" ", "_") ?: "video"
            _exportStatus.value = ExportStatus.Finished(
                fileSizeBytes = 28_400_000L, // ~28.4 MB
                renderTimeSeconds = 4.2f,
                fileName = "NovaCut_${projTitle}_1080p.mp4"
            )
        }
    }

    fun dismissExport() {
        _exportStatus.value = ExportStatus.Idle
    }

    // ================= Helpers =================

    fun getSelectedClip(): ClipItem? {
        val project = _currentProject.value ?: return null
        return project.clips.firstOrNull { it.id == _selectedClipId.value }
    }

    fun getActiveMainVideoClip(): ClipItem? {
        val project = _currentProject.value ?: return null
        val cur = _playheadMs.value
        return project.clips.firstOrNull {
            it.trackType == TrackType.VIDEO_MAIN && cur >= it.startMs && cur < it.endMs
        } ?: project.clips.firstOrNull { it.trackType == TrackType.VIDEO_MAIN }
    }

    fun getActivePipClip(): ClipItem? {
        val project = _currentProject.value ?: return null
        val cur = _playheadMs.value
        return project.clips.firstOrNull {
            it.trackType == TrackType.VIDEO_PIP && cur >= it.startMs && cur < it.endMs
        }
    }

    fun getActiveTextClip(): ClipItem? {
        val project = _currentProject.value ?: return null
        val cur = _playheadMs.value
        return project.clips.firstOrNull {
            it.trackType == TrackType.TEXT_GRAPHICS && cur >= it.startMs && cur < it.endMs
        }
    }

    fun interpolateKeyframeAt(clip: ClipItem, timeMs: Long): KeyframeItem {
        val keyframes = clip.keyframes.sortedBy { it.timeMs }
        if (keyframes.isEmpty()) {
            return KeyframeItem("default", timeMs, 0f, 0f, 1f, 0f, 1f)
        }
        if (timeMs <= keyframes.first().timeMs) {
            return keyframes.first()
        }
        if (timeMs >= keyframes.last().timeMs) {
            return keyframes.last()
        }

        var prev = keyframes.first()
        var next = keyframes.last()
        for (i in 0 until keyframes.size - 1) {
            if (timeMs >= keyframes[i].timeMs && timeMs <= keyframes[i + 1].timeMs) {
                prev = keyframes[i]
                next = keyframes[i + 1]
                break
            }
        }

        val range = (next.timeMs - prev.timeMs).toFloat().coerceAtLeast(1f)
        val rawT = (timeMs - prev.timeMs) / range
        val t = next.easing.interpolate(rawT)

        return KeyframeItem(
            id = "interpolated",
            timeMs = timeMs,
            posX = prev.posX + (next.posX - prev.posX) * t,
            posY = prev.posY + (next.posY - prev.posY) * t,
            scale = prev.scale + (next.scale - prev.scale) * t,
            rotation = prev.rotation + (next.rotation - prev.rotation) * t,
            opacity = prev.opacity + (next.opacity - prev.opacity) * t,
            easing = next.easing
        )
    }

    private fun updateProject(project: Project) {
        _currentProject.value = project
        ProjectRepository.saveProject(project)
    }
}
