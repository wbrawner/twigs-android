package com.wbrawner.budget

import android.app.Application

class AllowanceApplication : Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
                .baseUrl(BuildConfig.API_URL)
                .context(this)
                .build()
        appComponent.errorHandler.init(this)
    }
}
