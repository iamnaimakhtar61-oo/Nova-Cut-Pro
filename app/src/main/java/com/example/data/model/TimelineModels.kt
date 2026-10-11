package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class TrackType(val displayName: String, val canHaveAudio: Boolean) {
    VIDEO_MAIN("Main Video (A-Roll)", true),
    VIDEO_PIP("Overlay / B-Roll", true),
    TEXT_GRAPHICS("Text & Captions", false),
    AUDIO_BGM("Trending Audio (BGM)", true),
    AUDIO_SFX("Sound FX / Voice", true)
}

enum class EasingType(val label: String) {
    LINEAR("Linear"),
    EASE_IN("Ease In"),
    EASE_OUT("Ease Out"),
    BEZIER("Smooth Bezier"),
    BOUNCE("Dynamic Bounce");

    fun interpolate(t: Float): Float {
        val clamped = t.coerceIn(0f, 1f)
        return when (this) {
            LINEAR -> clamped
            EASE_IN -> clamped * clamped
            EASE_OUT -> clamped * (2f - clamped)
            BEZIER -> clamped * clamped * (3f - 2f * clamped)
            BOUNCE -> {
                if (clamped < 0.36364f) {
                    7.5625f * clamped * clamped
                } else if (clamped < 0.72727f) {
                    val post = clamped - 0.54545f
                    7.5625f * post * post + 0.75f
                } else if (clamped < 0.90909f) {
                    val post = clamped - 0.81818f
                    7.5625f * post * post + 0.9375f
                } else {
                    val post = clamped - 0.95455f
                    7.5625f * post * post + 0.984375f
                }
            }
        }
    }
}

data class KeyframeItem(
    val id: String,
    val timeMs: Long,
    val posX: Float = 0f,       // -1f to 1f normalized offset
    val posY: Float = 0f,       // -1f to 1f normalized offset
    val scale: Float = 1.0f,    // 0.2f to 3.0f
    val rotation: Float = 0f,   // -180f to 180f
    val opacity: Float = 1.0f,  // 0.0f to 1.0f
    val easing: EasingType = EasingType.BEZIER
)

enum class LutPreset(
    val title: String,
    val description: String,
    val tempShift: Float,
    val tintShift: Float,
    val contrastBoost: Float,
    val satBoost: Float,
    val previewTint: Long
) {
    NONE("Original", "Natural camera output", 0f, 0f, 0f, 1.0f, 0x00000000),
    TEAL_ORANGE("Teal & Orange", "Hollywood blockbuster grade", 0.35f, -0.15f, 0.25f, 1.3f, 0x3300E5FF),
    CYBERPUNK("Cyberpunk Neon", "High-saturation neon nights", -0.2f, 0.4f, 0.35f, 1.5f, 0x33FF2A6D),
    MOODY_FILM("Moody Film", "Low-contrast vintage matte", -0.1f, -0.05f, -0.15f, 0.85f, 0x338A2BE2),
    GOLDEN_HOUR("Golden Hour", "Sunset glow & warm amber tones", 0.5f, 0.1f, 0.15f, 1.2f, 0x33FFB703),
    VINTAGE_35MM("Vintage 35mm", "Kodak film emulation with grain", 0.2f, 0.15f, -0.05f, 0.9f, 0x33C89666),
    MONOCHROME_NOIR("Noir Cinema", "High contrast black & white", 0f, 0f, 0.4f, 0.0f, 0x33FFFFFF),
    CLEAN_VLOG("Clean Vlogger", "Bright skin tones and crisp whites", 0.1f, 0.05f, 0.1f, 1.15f, 0x3300F59B)
}

data class ColorGradingConfig(
    val exposure: Float = 0.0f,      // -1.0f .. 1.0f
    val contrast: Float = 0.0f,      // -1.0f .. 1.0f
    val saturation: Float = 1.0f,    // 0.0f .. 2.0f
    val temperature: Float = 0.0f,   // -1.0f (cool blue) .. 1.0f (warm amber)
    val tint: Float = 0.0f,          // -1.0f (green) .. 1.0f (magenta)
    val highlights: Float = 0.0f,    // -1.0f .. 1.0f
    val shadows: Float = 0.0f,       // -1.0f .. 1.0f
    val vignette: Float = 0.0f,      // 0.0f .. 1.0f
    val grain: Float = 0.0f,         // 0.0f .. 1.0f
    val lut: LutPreset = LutPreset.NONE,
    // 3-Way Color Wheels: hue angle (0..360), saturation (0..1)
    val liftHue: Float = 220f,
    val liftSat: Float = 0.0f,
    val gammaHue: Float = 45f,
    val gammaSat: Float = 0.0f,
    val gainHue: Float = 35f,
    val gainSat: Float = 0.0f
)

enum class EffectType(val label: String, val category: String) {
    GLITCH("Glitch Shift", "Retro"),
    RGB_SPLIT("RGB Aberration", "Distortion"),
    VHS_RETRO("VHS Scanlines", "Vintage"),
    LIGHT_LEAK("Dreamy Light Leak", "Light"),
    FILM_GRAIN("35mm Film Grain", "Texture"),
    ZOOM_BLUR("Radial Zoom Blur", "Motion"),
    EDGE_GLOW("Neon Edge Glow", "Stylize"),
    SHAKE("Beat Earthquake", "Motion")
}

data class EffectItem(
    val id: String,
    val type: EffectType,
    val intensity: Float = 0.7f,
    val startMs: Long = 0L,
    val endMs: Long = 0L
)

enum class TransitionType(val label: String, val icon: String) {
    WHIP_PAN("Whip Pan", "fast_forward"),
    ZOOM_IN("Smooth Zoom", "zoom_in"),
    CROSS_DISSOLVE("Dissolve", "opacity"),
    GLITCH_FLASH("Glitch Flash", "flash_on"),
    SLIDE_RIGHT("Slide", "trending_flat"),
    LIGHT_FLASH("Light Burst", "wb_sunny"),
    SPIN("Fast Spin", "rotate_right")
}

data class TransitionItem(
    val id: String,
    val type: TransitionType,
    val durationMs: Long = 400L
)

enum class CaptionStylePreset(
    val title: String,
    val previewWord: String,
    val highlightColor: Long,
    val textColor: Long,
    val hasBackgroundBox: Boolean,
    val isBouncy: Boolean
) {
    HORMOZI("Viral Hormozi", "HIGHLIGHT", 0xFFFFD700, 0xFFFFFFFF, true, true),
    KARAOKE_BOUNCE("Karaoke Glow", "BOUNCING", 0xFF00F59B, 0xFF00E5FF, false, true),
    MINIMAL_PILL("Clean Pill", "Subtitles", 0xFFFFFFFF, 0xFF141620, true, false),
    NEON_CYBER("Cyber Neon", "PUNCHLINE", 0xFFFF2A6D, 0xFF00E5FF, false, false)
}

data class AutoCaptionItem(
    val id: String,
    val text: String,
    val startMs: Long,
    val endMs: Long,
    val isHookOrPunchline: Boolean = false
)

data class TextOverlayConfig(
    val text: String = "Viral Hook Title",
    val stylePreset: CaptionStylePreset = CaptionStylePreset.HORMOZI,
    val fontSizeSp: Float = 24f,
    val posX: Float = 0f,
    val posY: Float = 0.65f, // near bottom for TikTok safe area
    val rotation: Float = 0f,
    val scale: Float = 1.0f,
    val fontStyleName: String = "Impact Bold"
)

data class AudioTrack(
    val id: String,
    val title: String,
    val artist: String,
    val durationMs: Long,
    val bpm: Int,
    val waveformData: List<Float>,
    val isTrending: Boolean = true,
    val category: String = "TikTok Viral",
    val coverColor: Long = 0xFF8A2BE2
)

data class ClipItem(
    val id: String,
    val trackType: TrackType,
    val name: String,
    val startMs: Long,
    val endMs: Long,
    val sourceDurationMs: Long,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = sourceDurationMs,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val isMuted: Boolean = false,
    val visualTheme: String = "cyberpunk", // sample visual theme for rendering
    val colorGrading: ColorGradingConfig = ColorGradingConfig(),
    val effects: List<EffectItem> = emptyList(),
    val transition: TransitionItem? = null,
    val keyframes: List<KeyframeItem> = emptyList(),
    val textOverlay: TextOverlayConfig? = null,
    val captions: List<AutoCaptionItem> = emptyList(),
    val audioTrack: AudioTrack? = null
) {
    val durationMs: Long get() = (endMs - startMs).coerceAtLeast(100L)
}
