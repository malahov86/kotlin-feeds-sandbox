package ru.alex.feeds.service

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import ru.alex.feeds.repository.elastic.ArticleRepository
import ru.alex.feeds.repository.mongo.FeedItemRepository
import ru.alex.feeds.repository.mongo.entity.FeedItem

@Service
class FeedSimilarityService(
    private val articleRepository: ArticleRepository,
    private val feedItemRepository: FeedItemRepository
) {
    suspend fun findSimilar(feedItemId: String, size: Int): List<FeedItem> {
        val similarItemsScores = articleRepository.findSimilar(feedItemId, size)
            .collectMap({ it.id!! }, { it.score })
            .awaitSingle()

        return feedItemRepository.findByIdIn(similarItemsScores.keys).toList()
            .sortedByDescending { similarItemsScores[it.id] }
    }
}
