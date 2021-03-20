package dev.mk2481.kadai20200329.controllers

import dev.mk2481.kadai20200329.controllers.exceptions.BadRequestException
import dev.mk2481.kadai20200329.controllers.exceptions.NotFoundException
import dev.mk2481.kadai20200329.controllers.json.AuthorJSON
import dev.mk2481.kadai20200329.converters.toJSON
import dev.mk2481.kadai20200329.models.AuthorName
import dev.mk2481.kadai20200329.repositories.AuthorsRepository
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*

@Controller("/api/authors")
class AuthorController(
    private val repository: AuthorsRepository
) {
    @Get("/{?q}")
    fun index(q: String?): List<AuthorJSON> = repository.findAll(searchName = q).map { it.toJSON() }

    @Get("/{id}")
    fun findById(@PathVariable id: Int): AuthorJSON =
        repository.findById(id)?.toJSON() ?: throw NotFoundException("not found id=${id}")

    @Post("/")
    fun create(name: String): AuthorJSON {
        return kotlin.runCatching { AuthorName(name) }
            .fold(
                onSuccess = {
                    repository.create(it).toJSON()
                },
                onFailure = {
                    throw BadRequestException(it.message)
                }
            )
    }

    @Patch("/{id}")
    fun update(@PathVariable id: Int, name: String): HttpResponse<Any> {
        val author = repository.findById(id) ?: throw NotFoundException("not found id=${id}")
        return kotlin.runCatching { AuthorName(name) }
            .fold(
                onSuccess = {
                    repository.update(author.updateName(it))
                    HttpResponse.noContent()
                },
                onFailure = {
                    throw BadRequestException(it.message)
                }
            )
    }

    @Delete("/{id}")
    fun delete(@PathVariable id: Int): HttpResponse<Any> {
        val author = repository.findById(id) ?: throw NotFoundException("not found id=${id}")
        repository.delete(author)
        return HttpResponse.noContent()
    }
}