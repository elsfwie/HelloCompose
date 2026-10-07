package com.example.ch07.util

fun formatRelativeTime(now: Long, then: Long): String {
    val elapsed = (now - then).coerceAtLeast(0L)

    val minute = 60_000L
    val hour = 60 * minute
    val day = 24 * hour
    val month = 30 * day
    val year = 365 * day

    return when {
        elapsed < minute -> "baru saja"
        elapsed < hour -> "${elapsed / minute} menit lalu"
        elapsed < day -> "${elapsed / hour} jam lalu"
        elapsed < month -> "${elapsed / day} hari lalu"
        elapsed < year -> "${elapsed / month} bulan lalu"
        else -> "${elapsed / year} tahun lalu"
    }
}