package com.wbrawner.budget.ui.auth


import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.R
import com.wbrawner.budget.ui.AuthenticationState
import com.wbrawner.budget.ui.SplashViewModel
import com.wbrawner.budget.ui.ensureNotEmpty
import com.wbrawner.budget.ui.show
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {
    private val viewModel: SplashViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is AsyncState.Loading -> {
                        handleLoading(true)
                    }
                    is AsyncState.Error -> {
                        handleLoading(false)
                        username.error = "Invalid username/password"
                        password.error = "Invalid username/password"
                        state.exception.printStackTrace()
                    }
                    is AsyncState.Success -> {
                        if (state.data is AuthenticationState.Unauthenticated) {
                            server.setText(state.data.server ?: "")
                            username.setText(state.data.username ?: "")
                            password.setText(state.data.password ?: "")
                            state.data.errorMessage?.let {
                                AlertDialog.Builder(view.context)
                                    .setTitle("Login Failed")
                                    .setMessage(it)
                                    .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int -> }
                                    .show()
                            }
                        }
                    }
                }
            }
        }
        password.setOnEditorActionListener { _, _, _ ->
            submit.performClick()
        }
        submit.setOnClickListener {
            if (!server.ensureNotEmpty() || !username.ensureNotEmpty() || !password.ensureNotEmpty()) {
                return@setOnClickListener
            }
            viewModel.login(server.text.toString(), username.text.toString(), password.text.toString())
        }
        val serverString = arguments?.getString(EXTRA_SERVER)
        val usernameString = arguments?.getString(EXTRA_USERNAME)
        val passwordString = arguments?.getString(EXTRA_PASSWORD)
        if (!serverString.isNullOrBlank() && !usernameString.isNullOrBlank() && !passwordString.isNullOrBlank()) {
            server.setText(serverString)
            username.setText(usernameString)
            password.setText(passwordString)
            submit.performClick()
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        formPrompt.show(!isLoading)
        serverContainer.show(!isLoading)
        usernameContainer.show(!isLoading)
        passwordContainer.show(!isLoading)
        submit.show(!isLoading)
        registerButton.show(!isLoading)
        forgotPasswordLink.show(!isLoading)
        progressBar.show(isLoading)
    }

    companion object {
        const val EXTRA_SERVER = "server"
        const val EXTRA_USERNAME = "username"
        const val EXTRA_PASSWORD = "password"
    }
}
