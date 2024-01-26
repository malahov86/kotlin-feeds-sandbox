package ru.alex.feeds.dto

import ru.alex.feeds.repository.mongo.entity.Feed

data class FeedsList(
    val stats: FeedsStats,
    val feeds: List<Feed>
)

data class FeedsStats(
    val total: Int,
    val unreachable: Int
    // TODO inactive
)
