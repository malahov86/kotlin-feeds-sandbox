package ru.alex.feeds

import org.springframework.boot.fromApplication

fun main(args: Array<String>) {
    fromApplication<FeedsApplication>()
        .with(MongoTestConfiguration::class.java)
        .run(*args)
}
