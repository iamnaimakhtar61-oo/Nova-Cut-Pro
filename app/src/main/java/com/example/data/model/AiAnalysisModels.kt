package com.example.data.model

data class SilenceGap(
    val id: String,
    val startMs: Long,
    val endMs: Long,
    val isAutoTrimmed: Boolean = true
) {
    val durationMs: Long get() = (endMs - startMs).coerceAtLeast(0L)
}

data class SceneHighlight(
    val id: String,
    val label: String,
    val startMs: Long,
    val endMs: Long,
    val score: Float, // 0.0 to 1.0 (Hook score / Energy)
    val category: String, // "Hook", "Climax", "Talking Head", "B-Roll Action"
    val isSelectedForCut: Boolean = true
)

enum class RoughCutPreset(
    val title: String,
    val targetDurationMs: Long,
    val description: String,
    val pacingPpm: Int // cuts per minute
) {
    TIKTOK_15S("TikTok 15s High-Energy", 15_000L, "Rapid 1.5s cuts, dead silences deleted, instant hook", 35),
    REELS_30S("Reels 30s Story", 30_000L, "Punchy story arc, beat-matched transitions", 22),
    SHORTS_60S("Shorts 60s Deep Dive", 60_000L, "Tight talking head with dynamic B-roll cutaways", 18),
    VIRAL_BEAT_SYNC("Trending Beat Sync", 20_000L, "Synchronized exact cuts on kick/snare transients", 40)
}

data class AiAnalysisResult(
    val totalOriginalDurationMs: Long,
    val estimatedTrimmedDurationMs: Long,
    val silenceGaps: List<SilenceGap>,
    val highlights: List<SceneHighlight>,
    val beatCutPoints: List<Long>,
    val confidenceScore: Float = 0.94f,
    val suggestedPreset: RoughCutPreset = RoughCutPreset.TIKTOK_15S
)
