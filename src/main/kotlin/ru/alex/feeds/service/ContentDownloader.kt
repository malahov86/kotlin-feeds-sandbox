package ru.alex.feeds.service

import org.springframework.core.io.ByteArrayResource
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class ContentDownloader(
    private val webClient: WebClient
) {
    suspend fun asBytes(url: String): ByteArrayResource = getContent(url)

    suspend fun asString(url: String): String = getContent(url)

    private suspend inline fun <reified T : Any> getContent(url: String): T =
        webClient.get()
            .uri(url)
            .retrieve()
            .awaitBody<T>()
}
