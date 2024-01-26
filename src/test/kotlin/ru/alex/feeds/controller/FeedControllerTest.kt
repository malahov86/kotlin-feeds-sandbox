package ru.alex.feeds.controller

import kotlinx.coroutines.flow.count
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import ru.alex.feeds.MongoTestConfiguration
import ru.alex.feeds.controller.FeedController.Companion.FEEDS_UPLOAD_URL
import ru.alex.feeds.repository.mongo.FeedRepository
import ru.alex.feeds.utils.readFileAsString

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(MongoTestConfiguration::class)
class FeedControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var feedRepository: FeedRepository

    @Disabled
    @Test
    fun uploadSubscriptions() = runTest {
        val multipartPartBuilder = MultipartBodyBuilder().also {
            it.part("file", ClassPathResource("/ompl/ompl-2024-01-23.opml"))
                .contentType(MediaType.MULTIPART_FORM_DATA)
        }

        val response = webTestClient.post()
            .uri(FEEDS_UPLOAD_URL)
            .body(BodyInserters.fromMultipartData(multipartPartBuilder.build()))
            .exchange()

        response
            .expectStatus().isOk
            .expectBody()
            .json(readFileAsString("/__files/api/expected-response/subscriptions/upload-response.json"))

        assertEquals(263, feedRepository.findAll().count())
    }
}
