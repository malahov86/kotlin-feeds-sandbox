package ru.alex.feeds.service.mappers

import com.rometools.opml.feed.opml.Outline
import com.rometools.rome.feed.synd.SyndEntry
import ru.alex.feeds.repository.mongo.entity.Feed
import ru.alex.feeds.repository.mongo.entity.FeedItem

fun Outline.toFeed(): Feed =
    Feed(
        type = type,
        title = title,
        text = text,
        xmlUrl = xmlUrl,
        htmlUrl = htmlUrl
    )

fun SyndEntry.toFeedItem(): FeedItem =
    FeedItem(
        guid = link ?: links[0].href,
        title = title,
        uri = uri,
        link = link,
        author = author,
        keywords = categories.map { category -> category.name },
        summary = description?.let {
            FeedItem.Summary(
                type = it.type,
                value = it.value
            )
        },
        published = publishedDate?.toInstant()
    )
