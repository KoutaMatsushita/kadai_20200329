package dev.mk2481.kadai20200329.controllers

import dev.mk2481.kadai20200329.controllers.json.AuthorJSON
import dev.mk2481.kadai20200329.converters.toJSON
import dev.mk2481.kadai20200329.models.Author
import dev.mk2481.kadai20200329.models.AuthorName
import dev.mk2481.kadai20200329.repositories.AuthorsRepository
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
class AuthorControllerTest {

    @get:MockBean(AuthorsRepository::class)
    val mockRepository: AuthorsRepository = mockk()

    @Inject
    @field:Client("/api/authors")
    lateinit var client: HttpClient

    private val mockAuthors = (1..5).map { Author(it, AuthorName("author$it")) }

    @Test
    fun index() {
        every { mockRepository.findAll() } returns mockAuthors

        val result: List<AuthorJSON> =
            client.toBlocking().retrieve(HttpRequest.GET<Any>("/"), Argument.listOf(AuthorJSON::class.java))
        assertThat(result)
            .isNotEmpty
            .containsAll(mockAuthors.map { it.toJSON() })
    }

    @Test
    fun findById() {
        every { mockRepository.findById(1) } returns mockAuthors.first()

        val result: AuthorJSON =
            client.toBlocking().retrieve(HttpRequest.GET<Any>("/1"), Argument.of(AuthorJSON::class.java))
        assertThat(result)
            .isEqualTo(mockAuthors.first().toJSON())
    }

    @Test
    fun `findById_見つからない場合は404`() {
        every { mockRepository.findById(1) } returns null

        assertThatThrownBy { client.toBlocking().exchange("/1", String::class.java) }
            .isInstanceOf(HttpClientResponseException::class.java)
            .extracting { (it as HttpClientResponseException).status }
            .isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun create() {
        every { mockRepository.create(any()) } returns mockAuthors.first()

        val result: AuthorJSON = client.toBlocking().retrieve(
            HttpRequest.POST<Any>("/", mapOf("name" to mockAuthors.first().name.value)),
            Argument.of(AuthorJSON::class.java)
        )
        assertThat(result)
            .isEqualTo(mockAuthors.first().toJSON())
    }

    @Test
    fun `create_名前が短すぎると400`() {
        assertThatThrownBy {
            client.toBlocking().exchange(
                HttpRequest.POST("/", mapOf("name" to "")),
                String::class.java
            )
        }
            .isInstanceOf(HttpClientResponseException::class.java)
            .extracting { (it as HttpClientResponseException).status }
            .isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun update() {
        every { mockRepository.findById(1) } returns mockAuthors.first()
        every { mockRepository.update(mockAuthors.first()) } returns Unit

        assertThat(
            client.toBlocking().exchange(
                HttpRequest.PATCH<Any>("/1", mapOf("name" to mockAuthors.first().name.value)),
                Unit::class.java
            )
        )
            .extracting { it.status }
            .isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `update_存在しないIDが指定されていたら404`() {
        every { mockRepository.findById(1) } returns null

        assertThatThrownBy {
            client.toBlocking().exchange(
                HttpRequest.PATCH<Any>("/1", mapOf("name" to mockAuthors.first().name.value)),
                Unit::class.java
            )
        }
            .isInstanceOf(HttpClientResponseException::class.java)
            .extracting { (it as HttpClientResponseException).status }
            .isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `update_名前が短すぎると400`() {
        every { mockRepository.findById(1) } returns mockAuthors.first()

        assertThatThrownBy {
            client.toBlocking().exchange(
                HttpRequest.PATCH<Any>("/1", mapOf("name" to "")),
                Unit::class.java
            )
        }
            .isInstanceOf(HttpClientResponseException::class.java)
            .extracting { (it as HttpClientResponseException).status }
            .isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun delete() {
        every { mockRepository.findById(1) } returns mockAuthors.first()
        every { mockRepository.delete(mockAuthors.first()) } returns Unit

        assertThat(
            client.toBlocking().exchange(
                HttpRequest.DELETE<Any>("/1"),
                Unit::class.java
            )
        )
            .extracting { it.status }
            .isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `delete_存在しないIDが指定されていたら404`() {
        every { mockRepository.findById(1) } returns null

        assertThatThrownBy {
            client.toBlocking().exchange(
                HttpRequest.DELETE<Any>("/1"),
                Unit::class.java
            )
        }
            .isInstanceOf(HttpClientResponseException::class.java)
            .extracting { (it as HttpClientResponseException).status }
            .isEqualTo(HttpStatus.NOT_FOUND)
    }
}