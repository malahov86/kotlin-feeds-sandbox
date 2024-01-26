package ru.alex.feeds.service

import com.rometools.opml.feed.opml.Opml
import com.rometools.rome.io.WireFeedInput
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.count
import org.springframework.stereotype.Service
import ru.alex.feeds.dto.UploadResult
import ru.alex.feeds.repository.mongo.FeedRepository
import ru.alex.feeds.service.mappers.toFeed
import java.io.InputStream
import java.io.InputStreamReader

@Service
class FeedUploadService(
    private val feedRepository: FeedRepository
) {
    private val logger = KotlinLogging.logger {}

    suspend fun upload(inputStreamFile: InputStream): UploadResult {
        val opml = WireFeedInput().build(InputStreamReader(inputStreamFile)) as Opml

        feedRepository.deleteAll()

        return opml.outlines
            .filterNot { it.type.isNullOrEmpty() }
            .map { it.toFeed() }
            .let { feedRepository.saveAll(it) }
            .let {
                val uploaded = it.count()
                logger.info { "Successfully uploaded feeds $uploaded" }

                UploadResult(
                    total = opml.outlines.size,
                    uploaded = uploaded
                )
            }
    }
}
