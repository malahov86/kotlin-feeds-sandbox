package ru.alex.feeds.controller

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.alex.feeds.repository.mongo.FeedItemRepository
import ru.alex.feeds.repository.mongo.entity.FeedItem
import ru.alex.feeds.service.FeedSimilarityService

@RestController
class FeedItemController(
    private val feedItemRepository: FeedItemRepository,
    private val feedSimilarityService: FeedSimilarityService
) {
    @GetMapping(LATEST_FEED_ITEMS_URL)
    suspend fun getFeedItems(
        @RequestParam size: Int
    ): Flow<FeedItem> =
        feedItemRepository.findAllByOrderByPublishedDesc(
            PageRequest.of(0, size)
        )

    @GetMapping(SIMILAR_FEED_ITEMS_URL)
    suspend fun getSimilarFeedItems(
        @PathVariable itemId: String,
        @RequestParam size: Int
    ): List<FeedItem> =
        feedSimilarityService.findSimilar(itemId, size)

    companion object {
        const val LATEST_FEED_ITEMS_URL = "/items/latest"
        const val SIMILAR_FEED_ITEMS_URL = "/items/{itemId}/like"
    }
}
