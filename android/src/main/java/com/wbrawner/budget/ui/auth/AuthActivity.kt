package com.wbrawner.budget.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wbrawner.budget.ui.AuthViewModel
import com.wbrawner.budget.ui.MainActivity
import com.wbrawner.budget.ui.base.TwigsApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    val viewModel: AuthViewModel by viewModels()

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (viewModel.authenticated.value != null) {
                        Log.d("SplashScreen", "onPredraw Complete, removing listener")
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        Log.d("SplashScreen", "onPredraw active, ignoring draw")
                        false
                    }
                }
            }
        )
        setContent {
            TwigsApp {
                LaunchedEffect(key1 = viewModel) {
                    viewModel.checkForExistingCredentials()
                }
                LaunchedEffect(key1 = viewModel) {
                    viewModel.authenticated.collect {
                        if (it == true) {
                            // TODO: Replace with Compose Navigation
                            startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                            finish()
                        }
                    }
                }
                val navController = rememberNavController()
                NavHost(navController, AuthRoutes.LOGIN.name) {
                    composable(AuthRoutes.LOGIN.name) {
                        LoginScreen(navController = navController, viewModel = viewModel)
                    }
                    composable(AuthRoutes.REGISTER.name) {
                        Text("Not yet implemented")
                    }
                    composable(AuthRoutes.FORGOT_PASSWORD.name) {
                        Text("Not yet implemented")
                    }
                }
            }
        }
    }
}

enum class AuthRoutes {
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD,
}