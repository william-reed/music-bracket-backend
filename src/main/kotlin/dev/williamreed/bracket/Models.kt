package dev.williamreed.bracket

data class VideoData(
    val title: String,
    val youtubeId: String
)

data class Bracket(
    val title: String,
    val songs: List<VideoData>
)
