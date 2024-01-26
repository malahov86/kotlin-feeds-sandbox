package ru.alex.feeds.controller

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import ru.alex.feeds.dto.FeedsList
import ru.alex.feeds.dto.FeedsStats
import ru.alex.feeds.dto.UploadResult
import ru.alex.feeds.repository.mongo.entity.FeedItem
import ru.alex.feeds.repository.mongo.entity.State
import ru.alex.feeds.service.FeedProcessingService
import ru.alex.feeds.service.FeedService
import ru.alex.feeds.service.FeedUploadService

@RestController
class FeedController(
    private val feedService: FeedService,
    private val feedUploadService: FeedUploadService,
    private val feedProcessingService: FeedProcessingService
) {

    @GetMapping(FEEDS_URL)
    suspend fun getFeeds(): FeedsList =
        feedService.getFeeds().toList()
            .let { feeds ->
                FeedsList(
                    stats = FeedsStats(
                        total = feeds.size,
                        unreachable = feeds.count { it.state == State.DEAD }
                    ),
                    feeds = feeds
                )
            }

    @GetMapping(FEED_ITEMS_URL)
    suspend fun getFeedItems(
        @PathVariable feedId: String,
        @RequestParam size: Int
    ): Flow<FeedItem> =
        feedService.getFeedItems(feedId, size)

    /**
     * Upload RSS feeds
     *
     * OPML (Outline Processor Markup Language) is a format which allows you to share the RSS feeds you are following
     * in your Feedly with other applications.
     * @see <a href="http://dev.opml.org/">opml.org home</a>
     *
     * @param omplFile
     */
    @PostMapping(FEEDS_UPLOAD_URL, consumes = [MULTIPART_FORM_DATA_VALUE])
    suspend fun uploadFeeds(
        @RequestPart("file") omplFile: FilePart
    ): UploadResult =
        DataBufferUtils.join(omplFile.content())
            .awaitSingle()
            .asInputStream()
            .use { feedUploadService.upload(it) }

    @PostMapping(FEEDS_REFRESH_URL)
    suspend fun refreshFeeds() =
        feedProcessingService.updateFeeds()

    companion object {
        const val FEEDS_URL = "/feeds"
        const val FEEDS_UPLOAD_URL = "$FEEDS_URL/upload"
        const val FEEDS_REFRESH_URL = "$FEEDS_URL/refresh"
        const val FEED_ITEMS_URL = "/feeds/{feedId}/items"
    }
}
