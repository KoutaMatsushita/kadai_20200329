package dev.mk2481.kadai20200329.repositories

import dev.mk2481.kadai20200329.converters.toModel
import dev.mk2481.kadai20200329.db.Tables.AUTHORS
import dev.mk2481.kadai20200329.db.tables.pojos.JAuthors
import dev.mk2481.kadai20200329.models.Author
import dev.mk2481.kadai20200329.models.AuthorName
import org.jooq.DSLContext
import javax.inject.Singleton

@Singleton
class AuthorsRepository(
    private val dslContext: DSLContext
) {
    fun create(name: AuthorName): Author {
        return dslContext.insertInto(AUTHORS)
            .set(AUTHORS.NAME, name.value)
            .returning()
            .fetchOne()!!
            .into(JAuthors::class.java)
            .toModel()
    }

    fun findAll(searchName: String? = null): List<Author> {
        return dslContext.select()
            .from(AUTHORS)
            .apply {
                if (!searchName.isNullOrBlank()) {
                    where(AUTHORS.NAME.likeIgnoreCase("${searchName}%"))
                }
            }
            .fetchInto(JAuthors::class.java)
            .map { it.toModel() }
    }

    fun findById(id: Int): Author? {
        return dslContext.select()
            .from(AUTHORS)
            .where(AUTHORS.ID.eq(id))
            .fetchOneInto(JAuthors::class.java)
            ?.toModel()
    }

    fun update(author: Author) {
        dslContext.update(AUTHORS)
            .set(AUTHORS.NAME, author.name.value)
            .where(AUTHORS.ID.eq(author.id))
            .execute()
    }

    fun delete(author: Author) {
        dslContext.delete(AUTHORS)
            .where(AUTHORS.ID.eq(author.id))
            .execute()
    }
}