package ru.alex.feeds.controller

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onStart
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import ru.alex.feeds.repository.mongo.entity.FeedItem
import ru.alex.feeds.service.FeedItemService
import ru.alex.feeds.service.FeedProcessingService

@Controller
class FeedStreamController(
    private val feedItemService: FeedItemService,
    private val feedProcessingService: FeedProcessingService
) {
    @MessageMapping("feeds.stream")
    fun feedsStream(): Flow<FeedItem> = feedProcessingService
        .stream()
        .onStart {
            emitAll(feedItemService.getLatestItems(LATEST_ITEMS_NUM))
        }

    private companion object {
        const val LATEST_ITEMS_NUM = 10
    }
}
