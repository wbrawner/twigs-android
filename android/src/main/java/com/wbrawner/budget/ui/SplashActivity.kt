package com.wbrawner.budget.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.R
import com.wbrawner.budget.TwigsApplication
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as TwigsApplication).appComponent.inject(viewModel)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.decorView.apply {
            systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
        }
        val navController = findNavController(R.id.auth_content)
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is AsyncState.Success -> {
                        when (state.data) {
                            is AuthenticationState.Authenticated -> {
                                navController.navigate(R.id.mainActivity)
                                finish()
                            }
                            is AuthenticationState.Unauthenticated -> {
                                navController.navigate(R.id.loginFragment)
                            }
                        }
                    }
                    is AsyncState.Loading -> {}
                }
            }
        }
        lifecycleScope.launch {
            viewModel.checkForExistingCredentials()
        }
    }
}
