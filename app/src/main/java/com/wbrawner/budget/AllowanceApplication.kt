package com.wbrawner.budget

import android.app.Application
import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.di.AppComponent
import com.wbrawner.budget.di.DaggerAppComponent

class AllowanceApplication : Application() {
    var currentUser: User? = null
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
                .baseUrl(BuildConfig.API_URL)
                .context(this)
                .build()
    }
}
