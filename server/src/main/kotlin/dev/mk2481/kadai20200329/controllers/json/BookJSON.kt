package dev.mk2481.kadai20200329.controllers.json

data class BookJSON(
    val id: Int,
    val name: String,
    val author: AuthorJSON
)