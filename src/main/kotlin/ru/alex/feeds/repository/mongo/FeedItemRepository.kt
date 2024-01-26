package ru.alex.feeds.repository.mongo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import ru.alex.feeds.repository.mongo.entity.FeedItem

interface FeedItemRepository: CoroutineCrudRepository<FeedItem, String>, CoroutineSortingRepository<FeedItem, String> {
    suspend fun findByIdIn(ids: Set<String>): Flow<FeedItem>

    suspend fun existsByGuid(guid: String): Boolean

    suspend fun findAllByOrderByPublishedDesc(pageable: Pageable): Flow<FeedItem>

    suspend fun findAllByFeedIdOrderByPublishedDesc(feedId: String, pageable: Pageable): Flow<FeedItem>
}
