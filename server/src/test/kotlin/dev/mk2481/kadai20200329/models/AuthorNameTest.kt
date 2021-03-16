package dev.mk2481.kadai20200329.models

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test

class AuthorNameTest {
    @Test
    fun `正常系確認`() {
        assertThatCode { AuthorName("test") }
            .doesNotThrowAnyException()
    }

    @Test
    fun `空の文字列を渡すとエラーする`() {
        assertThatCode { AuthorName("") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("著者名は一文字以上である必要があります。")
    }
}