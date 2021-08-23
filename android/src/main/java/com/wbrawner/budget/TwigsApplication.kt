package com.wbrawner.budget

import android.app.Application
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.user.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class TwigsApplication : Application(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Main
    lateinit var appComponent: AppComponent
        private set
    @Inject
    lateinit var budgetRepository: BudgetRepository
    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
                .context(this)
                .build()
        appComponent.errorHandler.init(this)
        appComponent.inject(this)
        launch {
            try {
                userRepository.getProfile()
                budgetRepository.prefetchData()
            } catch (ignored: Exception) {
            }
        }
    }
}
