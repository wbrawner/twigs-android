package com.wbrawner.twigs.shared

import kotlinx.datetime.*

private const val CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

fun randomId(): String {
    val id = StringBuilder()
    repeat(32) {
        id.append(CHARACTERS.random())
    }
    return id.toString()
}

fun startOfMonth(now: Instant = Clock.System.now()): Instant =
    with(now.toLocalDateTime(TimeZone.UTC)) {
        LocalDateTime(year, month, 1, 0, 0, 0)
            .toInstant(TimeZone.UTC)
    }

fun endOfMonth(now: Instant = Clock.System.now()): Instant =
    with(now.toLocalDateTime(TimeZone.UTC)) {
        val (adjustedYear, adjustedMonth) = if (monthNumber == 12) {
            year + 1 to 1
        } else {
            year to monthNumber + 1
        }
        LocalDateTime(adjustedYear, adjustedMonth, 1, 23, 59, 59)
            .toInstant(TimeZone.UTC)
            .minus(1, DateTimeUnit.DAY, TimeZone.UTC)
    }