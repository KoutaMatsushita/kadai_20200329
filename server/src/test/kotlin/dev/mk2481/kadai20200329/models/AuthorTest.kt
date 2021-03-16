package dev.mk2481.kadai20200329.models

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuthorTest {
    @Test
    fun updateName() {
        val author = Author(1, AuthorName("test"))
        assertThat(author.updateName(AuthorName("newName")))
            .toString()
    }
}