package dev.mk2481.kadai20200329.models

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuthorTest {
    @Test
    fun updateName() {
        val oldName = AuthorName("test")
        val author = Author(1, oldName)
        val newName = AuthorName("newName")
        assertThat(author.updateName(newName))
            .isEqualTo(Author(1, newName))
            .describedAs("変更されていること")
        assertThat(author)
            .isEqualTo(Author(1, oldName))
            .describedAs("元のインスタンスが変更されていないこと")
    }
}