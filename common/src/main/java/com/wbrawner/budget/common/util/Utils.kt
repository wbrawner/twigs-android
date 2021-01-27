package com.wbrawner.budget.common.util

private const val CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

fun randomId(): String {
    val id = StringBuilder()
    repeat(32) {
        id.append(CHARACTERS.random())
    }
    return id.toString()
}