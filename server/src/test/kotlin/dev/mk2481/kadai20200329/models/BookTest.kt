package dev.mk2481.kadai20200329.models

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BookTest {
    @Test
    fun updateName() {
        val oldName = BookName("test")
        val author = Author(1, AuthorName("author1"))
        val book = Book(1, oldName, author)
        val newName = BookName("newName")
        assertThat(book.updateName(newName))
            .isEqualTo(Book(1, newName, author))
            .describedAs("変更されていること")
        assertThat(book)
            .isEqualTo(Book(1, oldName, author))
            .describedAs("元のインスタンスが変更されていないこと")
    }

    @Test
    fun updateAuthor() {
        val name = BookName("test")
        val author1 = Author(1, AuthorName("author1"))
        val book = Book(1, name, author1)
        val author2 = Author(2, AuthorName("author2"))
        assertThat(book.updateAuthor(author2))
            .isEqualTo(Book(1, name, author2))
            .describedAs("変更されていること")
        assertThat(book)
            .isEqualTo(Book(1, name, author1))
            .describedAs("元のインスタンスが変更されていないこと")
    }
}