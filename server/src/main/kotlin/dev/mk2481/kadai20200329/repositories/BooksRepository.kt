package dev.mk2481.kadai20200329.repositories

import dev.mk2481.kadai20200329.converters.toModel
import dev.mk2481.kadai20200329.db.Tables
import dev.mk2481.kadai20200329.db.tables.pojos.JAuthors
import dev.mk2481.kadai20200329.db.tables.pojos.JBooks
import dev.mk2481.kadai20200329.models.Author
import dev.mk2481.kadai20200329.models.Book
import dev.mk2481.kadai20200329.models.BookName
import org.jooq.*
import javax.inject.Singleton

@Singleton
class BooksRepository(
    private val dslContext: DSLContext
) {
    fun create(name: BookName, author: Author): Book {
        return dslContext.insertInto(Tables.BOOKS)
            .set(Tables.BOOKS.NAME, name.value)
            .set(Tables.BOOKS.AUTHOR_ID, author.id)
            .returning()
            .fetchOne()!!
            .id
            .let { findById(it)!! }
    }

    fun findAll(searchName: String? = null, author: Author? = null): List<Book> {
        return findQuery {
            apply {
                if (!searchName.isNullOrBlank()) {
                    where(Tables.BOOKS.NAME.likeIgnoreCase("${searchName}%"))
                }
            }.apply {
                if (author != null) {
                    where(Tables.BOOKS.AUTHOR_ID.eq(author.id))
                }
            }
        }
            .fetch { it.toBookModel() }
    }

    fun findById(id: Int): Book? {
        return findQuery { where(Tables.BOOKS.ID.eq(id)) }
            .fetchOne { it.toBookModel() }
    }

    fun update(book: Book) {
        dslContext.update(Tables.BOOKS)
            .set(Tables.BOOKS.NAME, book.name.value)
            .set(Tables.BOOKS.AUTHOR_ID, book.author.id)
            .where(Tables.BOOKS.ID.eq(book.id))
            .execute()
    }

    fun delete(book: Book) {
        dslContext.delete(Tables.BOOKS)
            .where(Tables.BOOKS.ID.eq(book.id))
            .execute()
    }

    private fun findQuery(query: (SelectJoinStep<Record>.() -> SelectConnectByStep<Record>)? = null) =
        dslContext.select()
            .from(Tables.BOOKS)
            .join(Tables.AUTHORS)
            .onKey()
            .apply { query?.invoke(this) }

    private fun Record.toBookModel(): Book {
        val author = into(Tables.AUTHORS).into(JAuthors::class.java).toModel()
        return into(Tables.BOOKS).into(JBooks::class.java).toModel(author)
    }
}