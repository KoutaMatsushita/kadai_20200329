package dev.mk2481.kadai20200329.models

import javax.annotation.CheckReturnValue

class Author(
    val id: Int,
    val name: AuthorName
) {
    @CheckReturnValue
    fun updateName(newName: AuthorName): Author = Author(id, newName)

    override fun toString(): String {
        return "Author(id=$id, name=$name)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Author) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}