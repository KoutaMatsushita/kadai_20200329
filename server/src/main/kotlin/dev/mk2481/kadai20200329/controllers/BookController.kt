package dev.mk2481.kadai20200329.controllers

import dev.mk2481.kadai20200329.controllers.exceptions.BadRequestException
import dev.mk2481.kadai20200329.controllers.exceptions.NotFoundException
import dev.mk2481.kadai20200329.controllers.json.BookJSON
import dev.mk2481.kadai20200329.converters.toJSON
import dev.mk2481.kadai20200329.models.BookName
import dev.mk2481.kadai20200329.repositories.AuthorsRepository
import dev.mk2481.kadai20200329.repositories.BooksRepository
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*

@Controller("/api/books")
class BookController(
    private val booksRepository: BooksRepository,
    private val authorsRepository: AuthorsRepository
) {
    @Get("/{?authorId,q}")
    fun index(authorId: Int?, q: String?): List<BookJSON> = if (authorId == null) {
        booksRepository.findAll(searchName = q).map { it.toJSON() }
    } else {
        val author = authorsRepository.findById(authorId) ?: throw NotFoundException("author not found id=${authorId}")
        booksRepository.findAll(searchName = q, author = author).map { it.toJSON() }
    }

    @Get("/{id}")
    fun findById(@PathVariable id: Int): BookJSON? = booksRepository.findById(id)?.toJSON()

    @Post("/")
    fun create(name: String, authorId: Int): BookJSON {
        val author = authorsRepository.findById(authorId) ?: throw NotFoundException("author not found id=${authorId}")
        return kotlin.runCatching { BookName(name) }
            .fold(
                onSuccess = {
                    booksRepository.create(it, author).toJSON()
                },
                onFailure = {
                    throw BadRequestException(it.message)
                }
            )
    }

    @Patch("/{id}")
    fun update(@PathVariable id: Int, name: String, authorId: Int): HttpResponse<Any> {
        val book = booksRepository.findById(id) ?: throw NotFoundException("book not found id=${id}")
        val author = authorsRepository.findById(authorId) ?: throw NotFoundException("author not found id=${authorId}")
        return kotlin.runCatching { BookName(name) }
            .fold(
                onSuccess = {
                    booksRepository.update(book.updateName(it).updateAuthor(author))
                    HttpResponse.noContent()
                },
                onFailure = {
                    throw BadRequestException(it.message)
                }
            )
    }

    @Delete("/{id}")
    fun delete(@PathVariable id: Int): HttpResponse<Any> {
        val book = booksRepository.findById(id) ?: throw NotFoundException("book not found id=${id}")
        booksRepository.delete(book)
        return HttpResponse.noContent()
    }
}