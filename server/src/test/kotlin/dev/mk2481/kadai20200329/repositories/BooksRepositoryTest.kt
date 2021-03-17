package dev.mk2481.kadai20200329.repositories

import com.github.database.rider.core.api.dataset.DataSet
import com.github.database.rider.core.api.dataset.SeedStrategy
import com.github.database.rider.junit5.api.DBRider
import dev.mk2481.kadai20200329.models.Author
import dev.mk2481.kadai20200329.models.AuthorName
import dev.mk2481.kadai20200329.models.BookName
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.jooq.DSLContext
import org.jooq.exception.DataAccessException
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
@DBRider
class BooksRepositoryTest {

    @Inject
    lateinit var ctx: DSLContext

    @Test
    @DataSet("authors.yml", strategy = SeedStrategy.CLEAN_INSERT)
    fun create() {
        val repository = BooksRepository(ctx)
        val name = BookName("本1")
        val author = Author(1, AuthorName("author1"))
        assertThat(repository.create(name, author))
            .isNotNull
            .extracting { it.name }
            .isEqualTo(name)
    }

    @Test
    fun `create_存在しないauthorが指定されるとエラーする`() {
        val repository = BooksRepository(ctx)
        val name = BookName("本1")
        val author = Author(Int.MAX_VALUE, AuthorName("author1"))
        assertThatThrownBy { repository.create(name, author) }
            .isInstanceOf(DataAccessException::class.java)
    }

    @Test
    @DataSet("authors.yml,books.yml", strategy = SeedStrategy.CLEAN_INSERT)
    fun findAll() {
        val repository = BooksRepository(ctx)
        assertThat(repository.findAll())
            .isNotEmpty
            .hasSize(3)
            .extracting<String> { it.name.value }
            .containsAll(listOf("book1", "book2", "book3"))
    }

    @Test
    @DataSet("authors.yml,books.yml", strategy = SeedStrategy.CLEAN_INSERT)
    fun findById() {
        val repository = BooksRepository(ctx)
        assertThat(repository.findById(1))
            .isNotNull
            .extracting { it!!.name.value }
            .isEqualTo("book1")
        assertThat(repository.findById(Int.MAX_VALUE))
            .isNull()
    }

    @Test
    @DataSet("authors.yml,books.yml", strategy = SeedStrategy.CLEAN_INSERT)
    fun update() {
        val repository = BooksRepository(ctx)
        val book = repository.findById(1)!!
        val newName = BookName("newbook")
        val author2 = AuthorsRepository(ctx).findById(2)!!

        assertThatCode { repository.update(book.updateName(newName)) }
            .doesNotThrowAnyException()
        assertThat(repository.findById(1))
            .isNotNull
            .extracting { it!!.name }
            .isEqualTo(newName)

        assertThatCode { repository.update(book.updateAuthor(author2)) }
            .doesNotThrowAnyException()
        assertThat(repository.findById(1))
            .isNotNull
            .extracting { it!!.author }
            .isEqualTo(author2)
    }

    @Test
    @DataSet("authors.yml,books.yml", strategy = SeedStrategy.CLEAN_INSERT)
    fun delete() {
        val repository = BooksRepository(ctx)
        assertThatCode { repository.delete(repository.findById(1)!!) }
            .doesNotThrowAnyException()
        assertThat(repository.findById(1))
            .isNull()
    }
}