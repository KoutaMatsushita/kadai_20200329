package dev.mk2481.kadai20200329.models

import javax.annotation.CheckReturnValue

class Book(
    val id: Int,
    val name: BookName,
    val author: Author
) {
    @CheckReturnValue
    fun updateName(newName: BookName): Book = Book(id, newName, author)

    @CheckReturnValue
    fun updateAuthor(newAuthor: Author): Book = Book(id, name, newAuthor)

    override fun toString(): String {
        return "Book(id=$id, name='$name', author=$author)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Book) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}