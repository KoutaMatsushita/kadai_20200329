package dev.mk2481.kadai20200329

import io.micronaut.runtime.Micronaut.*

fun main(args: Array<String>) {
    build()
        .args(*args)
        .packages("dev.mk2481.kadai20200329")
        .start()
}

