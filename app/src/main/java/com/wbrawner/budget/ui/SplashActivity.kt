package com.wbrawner.budget.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.di.BudgetViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SplashActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Main
    private lateinit var viewModel: SplashViewModel
    @Inject
    lateinit var viewModelFactory: BudgetViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.decorView.apply {
            systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
        }
        (application as AllowanceApplication).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SplashViewModel::class.java)
        val navController = findNavController(R.id.auth_content)
        launch {
            val navId = try {
                val user = viewModel.checkForExistingCredentials()
                if (user != null) {
                    (application as AllowanceApplication).currentUser = user
                    R.id.mainActivity
                } else {
                    R.id.loginFragment
                }
            } catch (e: Exception) {
                R.id.loginFragment
            }
            navController.navigate(navId)
        }
    }
}
