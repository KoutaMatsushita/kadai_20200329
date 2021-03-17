package dev.mk2481.kadai20200329.controllers.exceptions

import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException

class NotFoundException(message: String? = null): HttpStatusException(HttpStatus.NOT_FOUND, message)