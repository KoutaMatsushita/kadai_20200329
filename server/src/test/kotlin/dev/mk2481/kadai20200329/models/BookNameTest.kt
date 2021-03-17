package dev.mk2481.kadai20200329.models

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class BookNameTest {
    @Test
    fun `正常系確認`() {
        Assertions.assertThatCode { BookName("test") }
            .doesNotThrowAnyException()
    }

    @Test
    fun `空の文字列を渡すとエラーする`() {
        Assertions.assertThatCode { BookName("") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("書籍名は一文字以上である必要があります。")
    }
}