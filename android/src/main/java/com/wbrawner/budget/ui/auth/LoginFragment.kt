package com.wbrawner.budget.ui.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.ui.SplashViewModel
import com.wbrawner.budget.ui.ensureNotEmpty
import com.wbrawner.budget.ui.show
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Main
    private val viewModel: SplashViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            formPrompt.show(!isLoading)
            usernameContainer.show(!isLoading)
            passwordContainer.show(!isLoading)
            submit.show(!isLoading)
            registerButton.show(!isLoading)
            forgotPasswordLink.show(!isLoading)
            progressBar.show(isLoading)
        })
        password.setOnEditorActionListener { _, _, _ ->
            submit.performClick()
        }
        submit.setOnClickListener {
            if (!username.ensureNotEmpty() || !password.ensureNotEmpty()) {
                return@setOnClickListener
            }
            launch {
                try {
                    val user = viewModel.login(username.text.toString(), password.text.toString())
                    (requireActivity().application as AllowanceApplication).currentUser = user
                    findNavController().navigate(R.id.mainActivity)
                    activity?.finish()
                } catch (e: Exception) {
                    username.error = "Invalid username/password"
                    password.error = "Invalid username/password"
                    e.printStackTrace()
                }
            }
        }
        val usernameString = arguments?.getString(EXTRA_USERNAME)
        val passwordString = arguments?.getString(EXTRA_PASSWORD)
        if (!usernameString.isNullOrBlank() && !passwordString.isNullOrBlank()) {
            username.setText(usernameString)
            password.setText(passwordString)
            submit.performClick()
        }
    }

    companion object {
        const val EXTRA_USERNAME = "username"
        const val EXTRA_PASSWORD = "password"
    }
}
