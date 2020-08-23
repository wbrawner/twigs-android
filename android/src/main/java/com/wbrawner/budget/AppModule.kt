package com.wbrawner.budget

import com.wbrawner.budget.common.util.ErrorHandler
import com.wbrawner.budget.util.AcraErrorHandler
import dagger.Module
import dagger.Provides

@Module
class AppModule {
    @Provides
    fun provideErrorHandler(): ErrorHandler = AcraErrorHandler()
}