package dev.mk2481.kadai20200329.controllers

import dev.mk2481.kadai20200329.controllers.json.BookJSON
import dev.mk2481.kadai20200329.converters.toJSON
import dev.mk2481.kadai20200329.models.Author
import dev.mk2481.kadai20200329.models.AuthorName
import dev.mk2481.kadai20200329.models.Book
import dev.mk2481.kadai20200329.models.BookName
import dev.mk2481.kadai20200329.repositories.AuthorsRepository
import dev.mk2481.kadai20200329.repositories.BooksRepository
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
class BookControllerTest {

    @get:MockBean(BooksRepository::class)
    val mockBooksRepository: BooksRepository = mockk()

    @get:MockBean(AuthorsRepository::class)
    val mockAuthorsRepository: AuthorsRepository = mockk()

    @Inject
    @field:Client("/api/books")
    lateinit var client: HttpClient

    private val mockAuthors = (1..5).map { Author(it, AuthorName("author$it")) }
    private val mockBooks = (1..10).map { Book(it, BookName("book$it"), mockAuthors[it % 5]) }

    @Test
    fun index() {
        every { mockBooksRepository.findAll() } returns mockBooks

        val result: List<BookJSON> =
            client.toBlocking().retrieve(HttpRequest.GET<Any>("/"), Argument.listOf(BookJSON::class.java))
        Assertions.assertThat(result)
            .isNotEmpty
            .containsAll(mockBooks.map { it.toJSON() })
    }

    @Test
    fun findByAuthor() {
        val author = mockAuthors.first()
        val books = mockBooks.filter { it.author == author }
        every { mockAuthorsRepository.findById(1) } returns author
        every { mockBooksRepository.findAll(author = author) } returns books

        val result: List<BookJSON> =
            client.toBlocking().retrieve(HttpRequest.GET<Any>("/?authorId=1"), Argument.listOf(BookJSON::class.java))
        Assertions.assertThat(result)
            .isNotEmpty
            .containsAll(books.map { it.toJSON() })
    }

    @Test
    fun findByName() {
        val books = listOf(mockBooks.first())
        every { mockBooksRepository.findAll(searchName = "book1") } returns books

        val result: List<BookJSON> =
            client.toBlocking().retrieve(HttpRequest.GET<Any>("/?q=book1"), Argument.listOf(BookJSON::class.java))
        Assertions.assertThat(result)
            .isNotEmpty
            .containsAll(books.map { it.toJSON() })

        verify { mockBooksRepository.findAll(searchName = "book1") }
        confirmVerified(mockBooksRepository)
    }

    @Test
    fun findByNameAndAuthor() {
        val author = mockAuthors.first()
        val books = mockBooks.filter { it.author == author }
        every { mockAuthorsRepository.findById(1) } returns author
        every { mockBooksRepository.findAll(searchName = "book1", author = author) } returns books

        val result: List<BookJSON> =
            client.toBlocking().retrieve(HttpRequest.GET<Any>("/?q=book1&authorId=1"), Argument.listOf(BookJSON::class.java))
        Assertions.assertThat(result)
            .isNotEmpty
            .containsAll(books.map { it.toJSON() })

        verify { mockBooksRepository.findAll(searchName = "book1", author = author) }
        confirmVerified(mockBooksRepository)
    }

    @Test
    fun findByAuthor_著者が見つからない場合は404() {
        every { mockAuthorsRepository.findById(any()) } returns null

        assertThatThrownBy { client.toBlocking().exchange("/?authorId=1", String::class.java) }
            .isInstanceOf(HttpClientResponseException::class.java)
            .extracting { (it as HttpClientResponseException).status }
            .isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun create() {
        val author = mockAuthors.first()
        val book = mockBooks.first()
        every { mockAuthorsRepository.findById(any()) } returns author
        every { mockBooksRepository.create(any(), any()) } returns book

        val result: BookJSON = client.toBlocking().retrieve(
            HttpRequest.POST<Any>("/", mapOf("name" to book.name.value, "authorId" to "1")),
            Argument.of(BookJSON::class.java)
        )
        Assertions.assertThat(result)
            .isEqualTo(book.toJSON())
    }

    @Test
    fun `create_名前が短すぎると400`() {
        assertThatThrownBy {
            client.toBlocking().exchange(
                HttpRequest.POST("/", mapOf("name" to "", "authorId" to "1")),
                String::class.java
            )
        }
            .isInstanceOf(HttpClientResponseException::class.java)
            .extracting { (it as HttpClientResponseException).status }
            .isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `create_著者が存在しない場合は404`() {
        every { mockAuthorsRepository.findById(any()) } returns null

        assertThatThrownBy {
            client.toBlocking().exchange(
                HttpRequest.POST("/", mapOf("name" to "test", "authorId" to "${Int.MAX_VALUE}")),
                String::class.java
            )
        }
            .isInstanceOf(HttpClientResponseException::class.java)
            .extracting { (it as HttpClientResponseException).status }
            .isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun update() {
        val author = mockAuthors.last()
        every { mockBooksRepository.findById(1) } returns mockBooks.first()
        every { mockAuthorsRepository.findById(any()) } returns author
        every { mockBooksRepository.update(any()) } returns Unit

        Assertions.assertThat(
            client.toBlocking().exchange(
                HttpRequest.PATCH<Any>("/1", mapOf("name" to author.name.value, "authorId" to author.id)),
                Unit::class.java
            )
        )
            .extracting { it.status }
            .isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `update_存在しないIDが指定されていたら404`() {
        val author = mockAuthors.last()
        every { mockBooksRepository.findById(any()) } returns null

        assertThatThrownBy {
            client.toBlocking().exchange(
                HttpRequest.PATCH<Any>("/1", mapOf("name" to author.name.value, "authorId" to author.id)),
                Unit::class.java
            )
        }
            .isInstanceOf(HttpClientResponseException::class.java)
            .extracting { (it as HttpClientResponseException).status }
            .isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `update_存在しない著者が指定されていたら404`() {
        val author = mockAuthors.last()
        every { mockBooksRepository.findById(any()) } returns mockBooks.first()
        every { mockAuthorsRepository.findById(any()) } returns null

        assertThatThrownBy {
            client.toBlocking().exchange(
                HttpRequest.PATCH<Any>("/1", mapOf("name" to author.name.value, "authorId" to author.id)),
                Unit::class.java
            )
        }
            .isInstanceOf(HttpClientResponseException::class.java)
            .extracting { (it as HttpClientResponseException).status }
            .isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `update_名前が短すぎると400`() {
        val author = mockAuthors.last()
        every { mockBooksRepository.findById(1) } returns mockBooks.first()
        every { mockAuthorsRepository.findById(any()) } returns author

        assertThatThrownBy {
            client.toBlocking().exchange(
                HttpRequest.PATCH<Any>("/1", mapOf("name" to "", "authorId" to author.id)),
                Unit::class.java
            )
        }
            .isInstanceOf(HttpClientResponseException::class.java)
            .extracting { (it as HttpClientResponseException).status }
            .isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun delete() {
        every { mockBooksRepository.findById(1) } returns mockBooks.first()
        every { mockBooksRepository.delete(mockBooks.first()) } returns Unit

        Assertions.assertThat(
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
        every { mockBooksRepository.findById(1) } returns null

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