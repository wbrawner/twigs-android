package com.wbrawner.budget.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.wbrawner.twigs.shared.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val store: Store
) : ViewModel() {
    val server = mutableStateOf("")
    val username = mutableStateOf("")
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val confirmPassword = mutableStateOf("")
    val enableLogin = mutableStateOf(false)
}
