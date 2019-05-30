package com.wbrawner.budget.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.di.BudgetViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {
    private val disposables = CompositeDisposable()
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
        viewModel.checkForExistingCredentials()
                .fromBackgroundToMain()
                .subscribe { hasExistingCredentials, err ->
                    if (hasExistingCredentials) {
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    } else {
                        showLogin()
                    }
                }
                .autoDispose(disposables)
    }

    private fun showLogin() {
        loginContainer.show()
        viewModel.isLoading
                .fromBackgroundToMain()
                .subscribe { isLoading ->
                    formPrompt.show(!isLoading)
                    usernameContainer.show(!isLoading)
                    passwordContainer.show(!isLoading)
                    submit.show(!isLoading)
                    progressBar.show(isLoading)
                }
                .autoDispose(disposables)
        submit.setOnClickListener {
            if (!username.ensureNotEmpty() || !password.ensureNotEmpty()) {
                return@setOnClickListener
            }
            viewModel.login(username.text.toString(), password.text.toString())
                    .fromBackgroundToMain()
                    .subscribe { success, error ->
                        if (error != null || !success) {
                            username.error = "Invalid username/password"
                            password.error = "Invalid username/password"
                            return@subscribe
                        }

                        startActivity(Intent(applicationContext, MainActivity::class.java))
                    }
                    .autoDispose(disposables)
        }

    }

    override fun onDestroy() {
        disposables.dispose()
        disposables.clear()
        super.onDestroy()
    }
}
