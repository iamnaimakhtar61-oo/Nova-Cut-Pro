package com.example.data.repository

import com.example.data.model.AudioTrack
import com.example.data.model.EffectItem
import com.example.data.model.EffectType
import com.example.data.model.LutPreset
import com.example.data.model.TransitionItem
import com.example.data.model.TransitionType

object MediaLibraryRepository {

    val trendingAudioList = listOf(
        AudioTrack(
            id = "audio_1",
            title = "Midnight Cyber Drift",
            artist = "Kavinsky Wave",
            durationMs = 24_000L,
            bpm = 126,
            waveformData = generateWaveform(seed = 42, length = 64, peakInterval = 8),
            isTrending = true,
            category = "Synthwave / TikTok Hot",
            coverColor = 0xFF00E5FF
        ),
        AudioTrack(
            id = "audio_2",
            title = "Neon Phonk Energy",
            artist = "Kordhell & Drift King",
            durationMs = 18_000L,
            bpm = 142,
            waveformData = generateWaveform(seed = 99, length = 64, peakInterval = 4),
            isTrending = true,
            category = "High Octane / Gym Reels",
            coverColor = 0xFFFF2A6D
        ),
        AudioTrack(
            id = "audio_3",
            title = "Golden Hour Lo-Fi Beats",
            artist = "Chillhop Sessions",
            durationMs = 30_000L,
            bpm = 85,
            waveformData = generateWaveform(seed = 17, length = 64, peakInterval = 12),
            isTrending = true,
            category = "Vlog / Aesthetic",
            coverColor = 0xFFFFB703
        ),
        AudioTrack(
            id = "audio_4",
            title = "Future Bass Bounce",
            artist = "San Holo Sound",
            durationMs = 22_000L,
            bpm = 130,
            waveformData = generateWaveform(seed = 73, length = 64, peakInterval = 6),
            isTrending = true,
            category = "Dance / Viral Cuts",
            coverColor = 0xFF8A2BE2
        ),
        AudioTrack(
            id = "audio_5",
            title = "Trap 808 Minimal Hit",
            artist = "Metro Vibe",
            durationMs = 20_000L,
            bpm = 140,
            waveformData = generateWaveform(seed = 55, length = 64, peakInterval = 5),
            isTrending = false,
            category = "Shorts / Punchy",
            coverColor = 0xFF00F59B
        )
    )

    val soundEffectsList = listOf(
        "Cinematic Whoosh Transition" to "whoosh",
        "Deep Sub Bass Boom" to "boom",
        "Camera Shutter & Flash" to "camera",
        "Glitch Digital Stutter" to "glitch",
        "Vinyl Record Scratch" to "vinyl",
        "Pop Bubble Notification" to "pop",
        "Cash Register Cha-Ching" to "cash",
        "Success Bell Ding" to "ding"
    )

    val stockFootageThemes = listOf(
        StockFootage(
            id = "stock_cyber",
            title = "Cyberpunk City Drive",
            category = "Urban / Neon",
            durationMs = 12_000L,
            dominantColors = listOf(0xFF0F172A, 0xFF00E5FF, 0xFFFF2A6D),
            description = "High motion, fast cars, glowing neon billboards"
        ),
        StockFootage(
            id = "stock_skate",
            title = "Sunset Golden Skateboard",
            category = "Action / Sports",
            durationMs = 15_000L,
            dominantColors = listOf(0xFF2E1065, 0xFFFFB703, 0xFFFF6B35),
            description = "Dynamic ollie in low sunset angle with lens flare"
        ),
        StockFootage(
            id = "stock_gym",
            title = "High Energy Gym Training",
            category = "Fitness / Motivation",
            durationMs = 10_000L,
            dominantColors = listOf(0xFF111827, 0xFF00F59B, 0xFF3B82F6),
            description = "Intense deadlift and chalk burst in cinematic slow-mo"
        ),
        StockFootage(
            id = "stock_vlog",
            title = "Aesthetic Matcha & Cafe",
            category = "Lifestyle / Vlog",
            durationMs = 14_000L,
            dominantColors = listOf(0xFFF3F4F6, 0xFF10B981, 0xFFD97706),
            description = "Clean pour-over coffee, morning sunlight, minimalist desk"
        ),
        StockFootage(
            id = "stock_tech",
            title = "Titanium Phone Unboxing",
            category = "Tech / Review",
            durationMs = 11_000L,
            dominantColors = listOf(0xFF18181B, 0xFF71717A, 0xFFE4E4E7),
            description = "Macro studio lighting, peel plastic protective film"
        )
    )

    fun getTransitionsList(): List<TransitionItem> =
        TransitionType.entries.map {
            TransitionItem(id = "tr_${it.name}", type = it, durationMs = 350L)
        }

    fun getEffectsList(): List<EffectItem> =
        EffectType.entries.map {
            EffectItem(id = "fx_${it.name}", type = it, intensity = 0.75f)
        }

    private fun generateWaveform(seed: Int, length: Int, peakInterval: Int): List<Float> {
        val result = mutableListOf<Float>()
        var current = 0.4f
        for (i in 0 until length) {
            val isPeak = (i % peakInterval == 0)
            val base = if (isPeak) 0.85f else 0.35f
            val variance = (((i * seed * 13) % 40) / 100f) - 0.2f
            current = (base + variance).coerceIn(0.15f, 0.98f)
            result.add(current)
        }
        return result
    }
}

data class StockFootage(
    val id: String,
    val title: String,
    val category: String,
    val durationMs: Long,
    val dominantColors: List<Long>,
    val description: String
)
