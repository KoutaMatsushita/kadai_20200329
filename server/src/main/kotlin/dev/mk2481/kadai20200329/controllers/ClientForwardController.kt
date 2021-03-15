package dev.mk2481.kadai20200329.controllers

import io.micronaut.core.io.ResourceResolver
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.server.types.files.StreamedFile
import java.util.*

/**
 * /api/**/* 以外かつ、静的ファイルが見つからないものを index.html にリダイレクトする。
 */
@Controller
class ClientForwardController(
    private val resourceResolver: ResourceResolver
) {
    @Get(value = "/", produces = [MediaType.TEXT_HTML])
    fun index(): StreamedFile =
        resourceResolver.getResource("classpath:public/index.html")
            .toNullable()
            ?.let { StreamedFile(it) }
            ?: throw Exception("not found index.html")

    @Get(value = "/{path:^(?!.*/api/?).*$}", produces = [MediaType.TEXT_HTML])
    fun forward(path: String): StreamedFile =
        resourceResolver.getResource("classpath:public/${path}").toNullable()
            ?.let { StreamedFile(it) }
            ?: index()
}

fun <T> Optional<T>.toNullable(): T? = this.orElse(null)