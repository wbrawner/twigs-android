package com.wbrawner.twigs.shared

import kotlinx.datetime.toInstant
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.Test

class UtilsTests {
    @Test
    fun `startOfMonth returns the correct dates`() {
        listOf(
            "2022-01-01T00:00:00Z" to "2022-01-01T00:00:00Z",
            "2022-02-01T00:00:00Z" to "2022-02-10T12:30:45Z",
            "2022-03-01T00:00:00Z" to "2022-03-15T12:30:45Z",
            "2022-04-01T00:00:00Z" to "2022-04-20T12:30:45Z",
            "2022-05-01T00:00:00Z" to "2022-05-20T12:30:45Z",
            "2022-06-01T00:00:00Z" to "2022-06-20T12:30:45Z",
            "2022-07-01T00:00:00Z" to "2022-07-20T12:30:45Z",
            "2022-08-01T00:00:00Z" to "2022-08-20T12:30:45Z",
            "2022-09-01T00:00:00Z" to "2022-09-20T12:30:45Z",
            "2022-10-01T00:00:00Z" to "2022-10-20T12:30:45Z",
            "2022-11-01T00:00:00Z" to "2022-11-20T12:30:45Z",
            "2022-12-01T00:00:00Z" to "2022-12-31T23:59:59Z"
        ).forEach { (expected, now) ->
            assertEquals(
                "Failed to set $now to start of month",
                expected,
                startOfMonth(now.toInstant()).toString()
            )
        }
    }

    @Test
    fun `endOfMonth returns the correct dates`() {
        listOf(
            "2022-01-31T23:59:59Z" to "2022-01-01T00:00:00Z",
            "2022-02-28T23:59:59Z" to "2022-02-10T12:30:45Z",
            "2022-03-31T23:59:59Z" to "2022-03-15T12:30:45Z",
            "2022-04-30T23:59:59Z" to "2022-04-20T12:30:45Z",
            "2022-05-31T23:59:59Z" to "2022-05-20T12:30:45Z",
            "2022-06-30T23:59:59Z" to "2022-06-20T12:30:45Z",
            "2022-07-31T23:59:59Z" to "2022-07-20T12:30:45Z",
            "2022-08-31T23:59:59Z" to "2022-08-20T12:30:45Z",
            "2022-09-30T23:59:59Z" to "2022-09-20T12:30:45Z",
            "2022-10-31T23:59:59Z" to "2022-10-20T12:30:45Z",
            "2022-11-30T23:59:59Z" to "2022-11-20T12:30:45Z",
            "2022-12-31T23:59:59Z" to "2022-12-31T23:59:59Z"
        ).forEach { (expected, now) ->
            assertEquals(
                "Failed to set $now to end of month",
                expected,
                endOfMonth(now.toInstant()).toString()
            )
        }
    }
}