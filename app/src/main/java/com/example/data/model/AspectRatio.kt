package com.example.data.model

enum class AspectRatio(
    val label: String,
    val ratioFloat: Float, // width / height
    val platformHint: String,
    val iconName: String
) {
    RATIO_9_16("9:16", 9f / 16f, "TikTok / Reels / Shorts", "crop_portrait"),
    RATIO_1_1("1:1", 1f, "Instagram Feed", "crop_square"),
    RATIO_16_9("16:9", 16f / 9f, "YouTube Landscape", "crop_landscape"),
    RATIO_4_5("4:5", 4f / 5f, "Instagram Portrait", "crop_5_4");

    companion object {
        fun fromString(value: String): AspectRatio =
            entries.firstOrNull { it.name == value } ?: RATIO_9_16
    }
}
