package com.wbrawner.budget.common.util

import android.app.Application

interface ErrorHandler {
    fun init(application: Application)
    fun reportException(t: Throwable, message: String? = null)
}
