package dev.mk2481.kadai20200329.models

data class BookName(val value: String) {
    init {
        require(value.isNotBlank()) { "書籍名は一文字以上である必要があります。" }
    }
}