package ru.alex.feeds.repository.mongo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.alex.feeds.repository.mongo.entity.Feed
import java.time.Instant

interface FeedRepository: CoroutineCrudRepository<Feed, String> {
    suspend fun findByRefreshedAtIsNullOrRefreshedAtLessThan(refreshedAt: Instant): Flow<Feed>
}
