package ru.alex.feeds.service

import com.rometools.rome.io.SyndFeedInput
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import ru.alex.feeds.repository.elastic.ArticleRepository
import ru.alex.feeds.repository.elastic.entity.Article
import ru.alex.feeds.repository.mongo.FeedItemRepository
import ru.alex.feeds.repository.mongo.FeedRepository
import ru.alex.feeds.repository.mongo.entity.Feed
import ru.alex.feeds.repository.mongo.entity.FeedItem
import ru.alex.feeds.repository.mongo.entity.State
import ru.alex.feeds.service.mappers.toFeedItem
import java.io.InputStreamReader
import java.time.Duration
import java.time.Instant
import kotlin.time.measureTime

@Service
class FeedProcessingService(
    private val contentDownloader: ContentDownloader,
    private val feedRepository: FeedRepository,
    private val feedItemRepository: FeedItemRepository,
    private val articleRepository: ArticleRepository
) {
    private val logger = KotlinLogging.logger {}
    private val sender: MutableSharedFlow<FeedItem> = MutableSharedFlow()

    fun stream(): Flow<FeedItem> = sender

    suspend fun updateFeeds() = coroutineScope {
        val producer = feedsProducer()
        repeat(NUM_WORKERS) {
            feedsProcessor(it, producer)
        }
    }

    suspend fun CoroutineScope.feedsProducer(): ReceiveChannel<Feed> {
        val refreshedAt = Instant.now().minus(REFRESH_DELTA)
        return feedRepository.findByRefreshedAtIsNullOrRefreshedAtLessThan(refreshedAt)
            .produceIn(this)
    }

    fun CoroutineScope.feedsProcessor(id: Int, channel: ReceiveChannel<Feed>) = launch(Dispatchers.IO) {
        measureTime {
            for (feed in channel) {
                runCatching {
                    val items = fetchAndParseFeed(feed.xmlUrl)
                    items.forEach { item ->
                        val exists = feedItemRepository.existsByGuid(item.guid)
                        if (!exists) {
                            val savedItem = feedItemRepository.save(
                                item.apply {
                                    feedId = feed.id!!
                                    crawled = Instant.now()
                                }
                            )

                            sender.emit(item)

                            val articleContent = contentDownloader.asString(item.link)

                            articleRepository.save(
                                Article(
                                    id = savedItem.id!!,
                                    feedId = feed.id!!,
                                    content = articleContent
                                )
                            ).awaitSingle()
                        }
                    }
                }.onFailure {
                    feedRepository.save(
                        feed.apply {
                            lastError = it.stackTraceToString()
                            state = State.DEAD
                        }
                    )
                }.onSuccess {
                    feedRepository.save(
                        feed.apply {
                            refreshedAt = Instant.now()
                            lastError = null
                            state = State.ACTIVE
                        }
                    )
                }
            }
        }.also {
            logger.info { "Processor-$id finished in $it" }
        }
    }

    private suspend fun fetchAndParseFeed(url: String): List<FeedItem> =
        contentDownloader.asBytes(url)
            .let { body ->
                SyndFeedInput().build(InputStreamReader(body.inputStream))
                    .entries
                    .filterNot { it.title.isNullOrEmpty() || it.link.isNullOrEmpty() }
                    .map { it.toFeedItem() }
            }

    private companion object {
        const val NUM_WORKERS = 5
        val REFRESH_DELTA: Duration = Duration.ofSeconds(1)
    }
}
