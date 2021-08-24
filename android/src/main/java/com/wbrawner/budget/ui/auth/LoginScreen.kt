package com.wbrawner.budget.ui.auth

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wbrawner.budget.R
import com.wbrawner.budget.ui.AuthViewModel
import com.wbrawner.budget.ui.base.TwigsApp

@ExperimentalAnimationApi
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val loading by viewModel.loading.collectAsState()
    val server by viewModel.server.collectAsState()
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val enableLogin by viewModel.enableLogin.collectAsState()
    AnimatedVisibility(
        visible = !loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LoginForm(
            server,
            viewModel::setServer,
            username,
            viewModel::setUsername,
            password,
            viewModel::setPassword,
            enableLogin,
            { navController.navigate(AuthRoutes.FORGOT_PASSWORD.name) },
            { navController.navigate(AuthRoutes.REGISTER.name) },
            viewModel::login,
        )
    }
}

@Composable
fun LoginForm(
    server: String,
    setServer: (String) -> Unit,
    username: String,
    setUsername: (String) -> Unit,
    password: String,
    setPassword: (String) -> Unit,
    enableLogin: Boolean,
    forgotPassword: () -> Unit,
    register: () -> Unit,
    login: () -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Image(
            painter = painterResource(id = if (isSystemInDarkTheme()) R.drawable.ic_twigs_outline else R.drawable.ic_twigs_color),
            contentDescription = null
        )
        Text("Log in to manage your budgets")
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = server,
            onValueChange = setServer,
            placeholder = { Text("Server") }
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = setUsername,
            placeholder = { Text("Username") }
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = setPassword,
            placeholder = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = enableLogin,
            onClick = login
        ) {
            Text("Login")
        }
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = forgotPassword
        ) {
            Text("Forgot password?")
        }
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = register
        ) {
            Text("Need an account?")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LoginScreen_Preview() {
    TwigsApp {
        LoginForm("", {}, "", {}, "", {}, false, {}, {}, { })
    }
}

@Composable
@Preview(showBackground = true, device = Devices.PIXEL_C)
fun LoginScreen_PreviewTablet() {
    TwigsApp {
        LoginForm("", {}, "", {}, "", {}, false, {}, {}, { })
    }
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
fun LoginScreen_PreviewDark() {
    TwigsApp {
        LoginForm("", {}, "", {}, "", {}, false, {}, {}, { })
    }
}