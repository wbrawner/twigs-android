package com.wbrawner.budget

import android.app.Application

class AllowanceApplication : Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
                .context(this)
                .build()
        appComponent.errorHandler.init(this)
    }
}
