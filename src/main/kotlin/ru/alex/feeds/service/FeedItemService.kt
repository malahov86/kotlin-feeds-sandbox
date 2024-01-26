package ru.alex.feeds.service

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestParam
import ru.alex.feeds.repository.mongo.FeedItemRepository
import ru.alex.feeds.repository.mongo.entity.FeedItem

@Service
class FeedItemService(
    private val feedItemRepository: FeedItemRepository
) {
    suspend fun getLatestItems(
        @RequestParam size: Int
    ): Flow<FeedItem> =
        feedItemRepository.findAllByOrderByPublishedDesc(
            PageRequest.of(0, size)
        )
}
