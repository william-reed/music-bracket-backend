package dev.williamreed.bracket

import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Handle YouTube searches
 */
object YoutubeSearch {
    private const val MAX_RESULTS = 5L
    private val API_KEY = System.getenv("YOUTUBE_API_KEY") ?: error("YOUTUBE_API_KEY environment variable not set.")

    private val youtube by lazy {
        YouTube.Builder(NetHttpTransport(), JacksonFactory(), HttpRequestInitializer { })
            .setApplicationName("Music Bracket").build()
    }

    /**
     * Make a youtube video search for the given query string
     */
    suspend fun search(query: String): List<VideoData> {
        return withContext(Dispatchers.IO) {
            val search = youtube.search().list("id,snippet").apply {
                key = API_KEY
                q = query
                type = "video"
                fields = "items(id/kind,id/videoId,snippet/title)"
                order = "viewCount"
                maxResults = MAX_RESULTS
            }.execute()

            if (search.items == null) error("Nothing returned from search")

            search.items.map { searchResult ->
                val videoId = searchResult.id.videoId
                return@map VideoData(
                    searchResult.snippet.title,
                    videoId
                )
            }

            // TODO use for seeding at some point
            // fetch the view count (used for seeding), and map to a VideoData
//            search.items.map { searchResult ->
//                youtube.videos().list("statistics").apply {
//                    id = searchResult.id.videoId
//                    key = API_KEY
//                }
//                    .execute()
//                    .let { video ->
//                        val videoId = searchResult.id.videoId
//                        return@map VideoData(
//                            videoId,
//                            searchResult.snippet.title,
//                            "https://i.ytimg.com/vi/$videoId/hqdefault.jpg",
//                            video.items[0].statistics.viewCount.toString()
//                        )
//                    }
//            }
        }
    }
}
