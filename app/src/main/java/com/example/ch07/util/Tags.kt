package com.example.ch07.util

import java.util.Locale
fun parseTags(text: String): List<String> =
    text.split(",")
    .map { tag ->
        tag.trim()
            .trimStart('#')
            .trim()
            .lowercase(Locale.ROOT)
    }
    .filter { it.isNotBlank() }
    .distinct()

fun formatTags(tags: List<String>): String = tags.joinToString (", " )
