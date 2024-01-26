package ru.alex.feeds.repository.elastic

import reactor.core.publisher.Flux
import ru.alex.feeds.repository.elastic.entity.Article

interface CustomArticleRepository {
    fun findSimilar(feedItemId: String, size: Int): Flux<Article>
}
