package dev.mk2481.kadai20200329.repositories

import com.github.database.rider.core.api.dataset.DataSet
import com.github.database.rider.core.api.dataset.SeedStrategy
import com.github.database.rider.junit5.api.DBRider
import dev.mk2481.kadai20200329.models.AuthorName
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
@DBRider
class AuthorsRepositoryTest {

    @Inject
    lateinit var ctx: DSLContext

    @Test
    @DataSet(cleanBefore = true, cleanAfter = true)
    fun create() {
        val repository = AuthorsRepository(ctx)
        val name = AuthorName("author1")
        assertThat(repository.create(name))
            .isNotNull
            .extracting { it.name }
            .isEqualTo(name)
    }

    @Test
    @DataSet("authors.yml", strategy = SeedStrategy.CLEAN_INSERT)
    fun findAll() {
        val repository = AuthorsRepository(ctx)
        assertThat(repository.findAll())
            .isNotEmpty
            .hasSize(3)
            .extracting<String> { it.name.value }
            .containsAll(listOf("山田太郎", "鈴木次郎", "山本三郎"))
    }

    @Test
    @DataSet("authors.yml", strategy = SeedStrategy.CLEAN_INSERT)
    fun findByName() {
        val repository = AuthorsRepository(ctx)
        assertThat(repository.findAll(searchName = "山"))
            .isNotEmpty
            .hasSize(2)
            .extracting<String> { it.name.value }
            .containsAll(listOf("山田太郎", "山本三郎"))
    }

    @Test
    @DataSet("authors.yml", strategy = SeedStrategy.CLEAN_INSERT)
    fun findById() {
        val repository = AuthorsRepository(ctx)
        assertThat(repository.findById(1))
            .isNotNull
            .extracting { it!!.name.value }
            .isEqualTo("山田太郎")
    }

    @Test
    @DataSet("authors.yml", strategy = SeedStrategy.CLEAN_INSERT)
    fun `findById_見つからない場合はnull`() {
        val repository = AuthorsRepository(ctx)
        assertThat(repository.findById(Int.MAX_VALUE))
            .isNull()
    }

    @Test
    @DataSet("authors.yml", strategy = SeedStrategy.CLEAN_INSERT)
    fun update() {
        val repository = AuthorsRepository(ctx)
        val author = repository.findById(1)!!
        val newName = AuthorName("田中太郎")
        Assertions.assertThatCode { repository.update(author.updateName(newName)) }
            .doesNotThrowAnyException()
        assertThat(repository.findById(1))
            .isNotNull
            .extracting { it!!.name }
            .isEqualTo(newName)
    }

    @Test
    @DataSet("authors.yml,books.yml", strategy = SeedStrategy.CLEAN_INSERT)
    fun delete() {
        val repository = AuthorsRepository(ctx)
        Assertions.assertThatCode { repository.delete(repository.findById(1)!!) }
            .doesNotThrowAnyException()
        assertThat(repository.findById(1))
            .isNull()
    }
}