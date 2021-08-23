package com.wbrawner.budget.ui

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (viewModel.state.value is AsyncState.Success) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )
        setContentView(R.layout.activity_auth)
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
                    is AsyncState.Loading -> {
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.checkForExistingCredentials()
        }
    }
}
