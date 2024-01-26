package ru.alex.feeds.repository.mongo.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class Feed(
    @Id
    val id: String? = null,
    val type: String,
    val title: String,
    val text: String,
    val category: String? = null,
    val xmlUrl: String,
    val htmlUrl: String?,
    var refreshedAt: Instant? = null,
    var state: State = State.ACTIVE,
    var lastError: String? = null
)

enum class State {
    ACTIVE,
    DEAD,
    INACTIVE
}
