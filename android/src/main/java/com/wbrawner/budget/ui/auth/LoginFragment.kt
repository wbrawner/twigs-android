package com.wbrawner.budget.ui.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.R
import com.wbrawner.budget.ui.SplashViewModel
import com.wbrawner.budget.ui.ensureNotEmpty
import com.wbrawner.budget.ui.show
import kotlinx.android.synthetic.main.fragment_login.*

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
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
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
            }
        })
        password.setOnEditorActionListener { _, _, _ ->
            submit.performClick()
        }
        submit.setOnClickListener {
            if (!username.ensureNotEmpty() || !password.ensureNotEmpty()) {
                return@setOnClickListener
            }
            viewModel.login(username.text.toString(), password.text.toString())
        }
        val usernameString = arguments?.getString(EXTRA_USERNAME)
        val passwordString = arguments?.getString(EXTRA_PASSWORD)
        if (!usernameString.isNullOrBlank() && !passwordString.isNullOrBlank()) {
            username.setText(usernameString)
            password.setText(passwordString)
            submit.performClick()
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        formPrompt.show(!isLoading)
        usernameContainer.show(!isLoading)
        passwordContainer.show(!isLoading)
        submit.show(!isLoading)
        registerButton.show(!isLoading)
        forgotPasswordLink.show(!isLoading)
        progressBar.show(isLoading)
    }

    companion object {
        const val EXTRA_USERNAME = "username"
        const val EXTRA_PASSWORD = "password"
    }
}
