package com.wbrawner.budget.ui.auth

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wbrawner.budget.R
import com.wbrawner.budget.ui.AuthViewModel
import com.wbrawner.budget.ui.base.TwigsApp
import com.wbrawner.twigs.shared.Effect
import com.wbrawner.twigs.shared.Store
import com.wbrawner.twigs.shared.user.ConfigAction

@ExperimentalAnimationApi
@Composable
fun LoginScreen(
    store: Store,
    viewModel: AuthViewModel
) {
    val state by store.state.collectAsState()
    val effect by store.effects.collectAsState(initial = Effect.Empty)
    val (error, setError) = remember { mutableStateOf("") }
    (effect as? Effect.Error)?.let {
        setError(it.message)
    } ?: setError("")
    val (server, setServer) = viewModel.server
    val (username, setUsername) = viewModel.username
    val (password, setPassword) = viewModel.password
    AnimatedVisibility(
        visible = !state.loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LoginForm(
            error,
            server,
            setServer,
            username,
            setUsername,
            password,
            setPassword,
            true,
            { store.dispatch(ConfigAction.ForgotPasswordClicked) },
            { store.dispatch(ConfigAction.RegisterClicked) },
            {
                store.dispatch(ConfigAction.SetServer(server))
                store.dispatch(
                    ConfigAction.Login(
                        viewModel.username.value,
                        viewModel.password.value
                    )
                )
            },
        )
    }
    AnimatedVisibility(
        visible = state.loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginForm(
    error: String,
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
    val (serverInput, usernameInput, passwordInput, loginButton) = FocusRequester.createRefs()
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
        if (error.isNotBlank()) {
            Text(text = error, color = Color.Red)
        }
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(serverInput)
                .onPreviewKeyEvent {
                    if (it.type != KeyEventType.KeyDown) {
                        return@onPreviewKeyEvent false
                    }
                    if (it.key != Key.Tab) {
                        return@onPreviewKeyEvent false
                    }
                    if (it.isShiftPressed) {
                        return@onPreviewKeyEvent false
                    }
                    usernameInput.requestFocus()
                    true
                },
            value = server,
            onValueChange = setServer,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Uri,
                capitalization = KeyboardCapitalization.None,
                imeAction = ImeAction.Next
            ),
            placeholder = { Text("Server") },
            keyboardActions = KeyboardActions(onNext = {
                usernameInput.requestFocus()
            }),
            maxLines = 1
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(usernameInput)
                .onPreviewKeyEvent {
                    if (it.type != KeyEventType.KeyDown) {
                        return@onPreviewKeyEvent false
                    }
                    if (it.key != Key.Tab) {
                        return@onPreviewKeyEvent false
                    }
                    if (it.isShiftPressed) {
                        serverInput.requestFocus()
                    } else {
                        passwordInput.requestFocus()
                    }
                    true
                },
            value = username,
            onValueChange = setUsername,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.None,
                imeAction = ImeAction.Next
            ),
            placeholder = { Text("Username") },
            keyboardActions = KeyboardActions(onNext = {
                passwordInput.requestFocus()
            }),
            maxLines = 1
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordInput)
                .onPreviewKeyEvent {
                    if (it.type != KeyEventType.KeyDown) {
                        return@onPreviewKeyEvent false
                    }
                    when (it.key) {
                        Key.Tab -> {
                            if (it.isShiftPressed) {
                                usernameInput.requestFocus()
                            } else {
                                loginButton.requestFocus()
                            }
                            true
                        }

                        Key.Enter -> {
                            login()
                            true
                        }

                        else -> false
                    }
                },
            value = password,
            onValueChange = setPassword,
            placeholder = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                capitalization = KeyboardCapitalization.None,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                login()
            }),
            maxLines = 1
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(loginButton),
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
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
fun LoginScreen_Preview() {
    TwigsApp {
        LoginForm("", "", {}, "", {}, "", {}, false, {}, {}, { })
    }
}

@Composable
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Preview(showBackground = true, device = Devices.PIXEL_C, uiMode = UI_MODE_NIGHT_YES)
fun LoginScreen_PreviewTablet() {
    TwigsApp {
        LoginForm("", "", {}, "", {}, "", {}, false, {}, {}, { })
    }
}