package com.wbrawner.twigs.shared.user

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import com.wbrawner.twigs.shared.Action
import com.wbrawner.twigs.shared.Effect
import com.wbrawner.twigs.shared.Reducer
import com.wbrawner.twigs.shared.Route
import com.wbrawner.twigs.shared.State
import com.wbrawner.twigs.shared.network.APIService
import kotlinx.coroutines.launch

const val KEY_AUTH_TOKEN = "authToken"
const val KEY_BASE_URL = "baseUrl"
const val KEY_USER_ID = "userId"

sealed interface ConfigAction : Action {
    data class SetServer(val server: String) : ConfigAction
    data class Login(val username: String, val password: String) : ConfigAction
    data class TokenLogin(val baseUrl: String, val authToken: String, val userId: String) :
        ConfigAction

    data class TokenLoginFailed(val error: Exception) : ConfigAction
    data class LoginSuccess(val user: User) : ConfigAction
    data class LoginFailed(
        val username: String,
        val password: String,
        val error: Exception
    ) : ConfigAction

    data class Register(val username: String, val password: String, val confirmPassword: String) :
        ConfigAction

    data class RegistrationSuccess(val user: User) : ConfigAction
    data class RegistrationFailed(val user: User) : ConfigAction
    object Logout : ConfigAction
    object ForgotPasswordClicked : Action
    object RegisterClicked : Action
}

class ConfigReducer(private val apiService: APIService, private val settings: Settings) :
    Reducer() {
    init {
        run {
            val baseUrl = settings.getStringOrNull(KEY_BASE_URL) ?: return@run
            val authToken = settings.getStringOrNull(KEY_AUTH_TOKEN) ?: return@run
            val userId = settings.getStringOrNull(KEY_USER_ID) ?: return@run
            initialActions.addLast(ConfigAction.TokenLogin(baseUrl, authToken, userId))
        }
    }

    override fun reduce(action: Action, state: () -> State): State = when (action) {
        is ConfigAction.SetServer -> {
            var baseUrl = action.server
            if (!baseUrl.startsWith("http")) {
                baseUrl = "https://$baseUrl"
            }
            settings[KEY_BASE_URL] = baseUrl
            apiService.baseUrl = baseUrl
            state()
        }

        is ConfigAction.Login -> {
            launch {
                try {
                    val session = apiService.login(LoginRequest(action.username, action.password))
                    settings[KEY_AUTH_TOKEN] = session.token
                    apiService.authToken = session.token
                    val user = apiService.getUser(session.userId)
                    settings[KEY_USER_ID] = session.userId
                    dispatch(ConfigAction.LoginSuccess(user))
                } catch (e: Exception) {
                    dispatch(ConfigAction.LoginFailed(action.username, action.password, e))
                }
            }
            state().copy(loading = true)
        }

        is ConfigAction.TokenLogin -> {
            launch {
                try {
                    apiService.authToken = action.authToken
                    apiService.baseUrl = action.baseUrl
                    val user = apiService.getUser(action.userId)
                    dispatch(ConfigAction.LoginSuccess(user))
                } catch (e: Exception) {
                    dispatch(ConfigAction.TokenLoginFailed(e))
                }
            }
            state().copy(loading = true)
        }

        is ConfigAction.TokenLoginFailed -> {
            emit(Effect.Error(action.error.message ?: "Invalid auth token"))
            settings.remove(KEY_AUTH_TOKEN)
            settings.remove(KEY_BASE_URL)
            settings.remove(KEY_USER_ID)
            state().copy(loading = false)
        }

        is ConfigAction.LoginSuccess -> state().copy(user = action.user, route = Route.Overview)
        is ConfigAction.LoginFailed -> {
            emit(Effect.Error(action.error.message ?: "Login failed"))
            state().copy(loading = false)
        }

        is ConfigAction.Logout -> {
            settings.remove(KEY_AUTH_TOKEN)
            settings.remove(KEY_BASE_URL)
            settings.remove(KEY_USER_ID)
            state().copy(user = null, route = Route.Login)
        }

        else -> state()
    }
}