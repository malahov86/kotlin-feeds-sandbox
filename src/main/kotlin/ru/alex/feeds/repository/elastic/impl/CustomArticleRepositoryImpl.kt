package ru.alex.feeds.repository.elastic.impl

import co.elastic.clients.elasticsearch._types.query_dsl.MoreLikeThisQuery
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations
import reactor.core.publisher.Flux
import ru.alex.feeds.repository.elastic.CustomArticleRepository
import ru.alex.feeds.repository.elastic.entity.ARTICLE_INDEX_NAME
import ru.alex.feeds.repository.elastic.entity.Article


class CustomArticleRepositoryImpl(
    private val reactiveElasticsearchOperations: ReactiveElasticsearchOperations
) : CustomArticleRepository {

    override fun findSimilar(feedItemId: String, size: Int): Flux<Article> {
        val query = NativeQuery.builder()
            .withQuery { it.moreLikeThis(buildLikeQuery(feedItemId)) }
            .withMaxResults(size)
            .build()

        return reactiveElasticsearchOperations.search(query,Article::class.java)
            .map { it.content.apply { score = it.score }}
    }

    @Suppress("MagicNumber")
    private fun buildLikeQuery(docId: String) =
        MoreLikeThisQuery.Builder()
            .minTermFreq(1)
            .maxQueryTerms(100)
            .minDocFreq(1)
            .like {
                it.document { doc -> doc
                    .index(ARTICLE_INDEX_NAME)
                    .id(docId)
                }
            }
            .build()
}
