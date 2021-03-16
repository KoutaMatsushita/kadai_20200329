package dev.mk2481.kadai20200329.converters

import dev.mk2481.kadai20200329.db.tables.pojos.JAuthors
import dev.mk2481.kadai20200329.models.Author
import dev.mk2481.kadai20200329.models.AuthorName

fun JAuthors.toModel(): Author = Author(id, AuthorName(name))