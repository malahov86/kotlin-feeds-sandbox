package ru.alex.feeds.repository.mongo.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class FeedItem (
    @Id
    val id: String? = null,
    var feedId: String? = null,
    val guid: String,
    val title: String,
    val uri: String,
    val link: String,
    val author: String,
    val language: String? = null,
    val keywords: List<String>,
    val summary: Summary? = null,
    val content: String? = null,
    val published: Instant? = null,
    var crawled: Instant? = null
) {
    data class Summary(
        val type: String? = null,
        val value: String
    )
}
