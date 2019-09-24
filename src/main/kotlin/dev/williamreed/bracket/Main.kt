package dev.williamreed.bracket

import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube

const val SEARCH_QUERY = "earfquake"
const val MAX_RESULTS = 5L
val API_KEY = System.getenv("YOUTUBE_API_KEY") ?: error("YOUTUBE_API_KEY environment variable not set.")

data class VideoData(
    val videoId: String,
    val title: String,
    val thumbnail: String,
    val viewCount: String
)

fun main() {
    val youtube = YouTube.Builder(NetHttpTransport(), JacksonFactory(), HttpRequestInitializer { })
        .setApplicationName("Music Bracket").build()

    val search = youtube.search().list("id,snippet").apply {
        key = API_KEY
        q = SEARCH_QUERY
        type = "video"
        fields = "items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)"
        order = "viewCount"
        maxResults = MAX_RESULTS
    }.execute()

    if (search.items == null) error("Nothing returned from search")

    // fetch the view count (used for seeding), and map to a VideoData
    search.items.map { searchResult ->
        youtube.videos().list("statistics").apply {
            id = searchResult.id.videoId
            key = API_KEY
        }
            .execute()
            .let { video ->
                val videoId = searchResult.id.videoId
                return@map VideoData(
                    videoId,
                    searchResult.snippet.title,
                    "https://i.ytimg.com/vi/$videoId/hqdefault.jpg",
                    video.items[0].statistics.viewCount.toString()
                )
            }
    }.forEach { video ->
        println(
            """
            ================================
            ${video.title}
            ${video.videoId}
            ${video.viewCount}
            ${video.thumbnail}
        """.trimIndent()
        )
    }
}
