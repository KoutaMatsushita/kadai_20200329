package dev.mk2481.kadai20200329.controllers

import dev.mk2481.kadai20200329.controllers.json.AuthorJSON
import dev.mk2481.kadai20200329.converters.toJSON
import dev.mk2481.kadai20200329.models.AuthorName
import dev.mk2481.kadai20200329.repositories.AuthorsRepository
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.http.exceptions.HttpStatusException

@Controller("/api/authors")
class AuthorController(
    private val repository: AuthorsRepository
) {
    @Get("/")
    fun index(): List<AuthorJSON> = repository.findAll().map { it.toJSON() }

    @Get("/{id}")
    fun findById(@PathVariable id: Int): AuthorJSON =
        repository.findById(id)?.toJSON() ?: throw HttpStatusException(HttpStatus.NOT_FOUND, "not found id=${id}")

    @Post("/")
    fun create(name: String): AuthorJSON {
        return kotlin.runCatching { AuthorName(name) }
            .fold(
                onSuccess = {
                    repository.create(it).toJSON()
                },
                onFailure = {
                    throw HttpStatusException(HttpStatus.BAD_REQUEST, it.message)
                }
            )
    }

    @Patch("/{id}")
    fun update(@PathVariable id: Int, name: String): HttpResponse<Any> {
        val author = repository.findById(id) ?: throw HttpStatusException(HttpStatus.NOT_FOUND, "not found id=${id}")
        return kotlin.runCatching { AuthorName(name) }
            .fold(
                onSuccess = {
                    repository.update(author.updateName(it))
                    HttpResponse.noContent()
                },
                onFailure = {
                    throw HttpStatusException(HttpStatus.BAD_REQUEST, it.message)
                }
            )
    }

    @Delete("/{id}")
    fun delete(@PathVariable id: Int): HttpResponse<Any> {
        val author = repository.findById(id) ?: throw HttpStatusException(HttpStatus.NOT_FOUND, "not found id=${id}")
        repository.delete(author)
        return HttpResponse.noContent()
    }
}