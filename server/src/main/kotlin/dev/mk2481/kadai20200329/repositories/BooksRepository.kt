package dev.mk2481.kadai20200329.repositories

import dev.mk2481.kadai20200329.converters.toModel
import dev.mk2481.kadai20200329.db.Tables
import dev.mk2481.kadai20200329.db.tables.pojos.JAuthors
import dev.mk2481.kadai20200329.db.tables.pojos.JBooks
import dev.mk2481.kadai20200329.models.Author
import dev.mk2481.kadai20200329.models.Book
import dev.mk2481.kadai20200329.models.BookName
import org.jooq.DSLContext
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

    fun findAll(): List<Book> {
        return dslContext.select()
            .from(Tables.BOOKS)
            .join(Tables.AUTHORS)
            .onKey()
            .fetch {
                val author = it.into(Tables.AUTHORS).into(JAuthors::class.java).toModel()
                it.into(Tables.BOOKS).into(JBooks::class.java).toModel(author)
            }
    }

    fun findById(id: Int): Book? {
        return dslContext.select()
            .from(Tables.BOOKS)
            .join(Tables.AUTHORS)
            .onKey()
            .where(Tables.BOOKS.ID.eq(id))
            .fetchOne {
                val author = it.into(Tables.AUTHORS).into(JAuthors::class.java).toModel()
                it.into(Tables.BOOKS).into(JBooks::class.java).toModel(author)
            }
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
}