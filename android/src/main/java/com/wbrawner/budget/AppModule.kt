package com.wbrawner.budget

import android.app.Application
import android.util.Log
import com.wbrawner.budget.common.util.ErrorHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideErrorHandler(): ErrorHandler = object : ErrorHandler {
        override fun init(application: Application) {
            // no-op
        }

        override fun reportException(t: Throwable, message: String?) {
            Log.e("ErrorHandler", "Report exception: $message", t)
        }
    }
}