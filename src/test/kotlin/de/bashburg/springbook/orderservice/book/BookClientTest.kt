package de.bashburg.springbook.orderservice.book

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier

class BookClientTest {

    private lateinit var mockWebServer: MockWebServer

    private lateinit var bookClient: BookClient

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toUri().toString()).build()
        bookClient = BookClient(webClient)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `when Book exists then return Book`() {
        val bookIsbn = "1234567890"

        val mockResponse = MockResponse().addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(
                """
                    {
                        "author": "Lyra Silverstar",
                        "createdDate": "2024-08-15T13:36:27.622164Z",
                        "id": 1,
                        "isbn": ${bookIsbn},
                        "lastModifiedDate": "2024-08-15T13:36:27.622164Z",
                        "price": 9.9,
                        "publisher": null,
                        "title": "Northern Lights",
                        "version": 1
                    }
            """.trimIndent()
            )
        mockWebServer.enqueue(mockResponse)

        val book = bookClient.getBookbyIsbn(bookIsbn)

        StepVerifier
            .create(book)
            .expectNextMatches { it.isbn == bookIsbn }
            .verifyComplete()
    }
}