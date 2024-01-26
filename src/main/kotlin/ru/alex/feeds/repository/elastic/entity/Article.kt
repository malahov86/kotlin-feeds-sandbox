package ru.alex.feeds.repository.elastic.entity


import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.TermVector

const val ARTICLE_INDEX_NAME = "articles"

@Document(indexName = ARTICLE_INDEX_NAME)
data class Article(
    @Id
    val id: String? = null,
    val feedId: String,
    @Field(termVector = TermVector.yes)
    val content: String,
) : Scoreable {
    @Transient
    override var score: Float? = null
}
