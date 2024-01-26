package ru.alex.feeds.utils

fun readFileAsString(path: String): String =
    object {}.javaClass.getResource(path)?.readText() ?: error("Can't load resource at $path")
