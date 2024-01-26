package ru.alex.feeds.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import ru.alex.feeds.repository.mongo.FeedItemRepository
import ru.alex.feeds.repository.mongo.FeedRepository
import ru.alex.feeds.repository.mongo.entity.Feed
import ru.alex.feeds.repository.mongo.entity.FeedItem

@Service
class FeedService(
    private val feedRepository: FeedRepository,
    private val feedItemRepository: FeedItemRepository
) {
    private val logger = KotlinLogging.logger {}

    suspend fun getFeeds(): Flow<Feed> =
        feedRepository.findAll()

    suspend fun getFeedItems(feedId: String, size: Int): Flow<FeedItem> =
        feedItemRepository.findAllByFeedIdOrderByPublishedDesc(
            feedId,
            PageRequest.of(0, size)
        )
}
