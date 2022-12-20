package com.wbrawner.budget

import android.app.Application
import android.util.Log
import com.wbrawner.twigs.shared.ErrorHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideErrorHandler(): com.wbrawner.twigs.shared.ErrorHandler = object :
        com.wbrawner.twigs.shared.ErrorHandler {
        override fun init(application: Application) {
            // no-op
        }

        override fun reportException(t: Throwable, message: String?) {
            Log.e("ErrorHandler", "Report exception: $message", t)
        }
    }
}