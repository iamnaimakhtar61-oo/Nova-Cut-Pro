package com.example.data.model

data class Project(
    val id: String,
    val title: String,
    val aspectRatio: AspectRatio = AspectRatio.RATIO_9_16,
    val fps: Int = 30, // 24, 30, 60
    val resolutionLabel: String = "1080p FHD", // 720p, 1080p, 4K
    val clips: List<ClipItem> = emptyList(),
    val totalDurationMs: Long = 18_000L,
    val lastEditedEpochMs: Long = System.currentTimeMillis(),
    val isCloudSynced: Boolean = true,
    val cloudSyncTimestamp: String = "Just now",
    val thumbnailGradientColors: List<Long> = listOf(0xFF0D0E15, 0xFF1F2544),
    val audioDuckingEnabled: Boolean = true,
    val audioDuckingFactor: Float = 0.35f
) {
    val durationSeconds: Float get() = totalDurationMs / 1000f

    fun getClipsForTrack(trackType: TrackType): List<ClipItem> =
        clips.filter { it.trackType == trackType }.sortedBy { it.startMs }
}
