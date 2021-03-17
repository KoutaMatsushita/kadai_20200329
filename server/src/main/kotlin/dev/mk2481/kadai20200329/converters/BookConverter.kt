package dev.mk2481.kadai20200329.converters

import dev.mk2481.kadai20200329.controllers.json.BookJSON
import dev.mk2481.kadai20200329.db.tables.pojos.JBooks
import dev.mk2481.kadai20200329.models.Author
import dev.mk2481.kadai20200329.models.Book
import dev.mk2481.kadai20200329.models.BookName

fun JBooks.toModel(author: Author): Book = Book(id, BookName(name), author)
fun Book.toJSON(): BookJSON = BookJSON(id, name.value, author.toJSON())