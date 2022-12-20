package com.wbrawner.twigs.shared

interface ErrorHandler {
    fun reportException(t: Throwable, message: String? = null)
}
