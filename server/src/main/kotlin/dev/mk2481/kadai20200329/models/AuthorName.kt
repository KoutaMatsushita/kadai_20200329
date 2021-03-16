package dev.mk2481.kadai20200329.models

data class AuthorName(val value: String) {
    init {
        require(value.isNotBlank()) { "著者名は一文字以上である必要があります。" }
    }
}