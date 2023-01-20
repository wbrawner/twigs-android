package com.wbrawner.budget

import android.util.Log
import com.wbrawner.pihelper.shared.create
import com.wbrawner.twigs.shared.ErrorHandler
import com.wbrawner.twigs.shared.Store
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideErrorHandler(): ErrorHandler = object : ErrorHandler {
        override fun reportException(t: Throwable, message: String?) {
            Log.e("ErrorHandler", "Report exception: $message", t)
        }
    }

    @Provides
    fun providesStore() = Store.create()
}