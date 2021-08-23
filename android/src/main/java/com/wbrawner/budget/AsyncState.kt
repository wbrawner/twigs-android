package com.wbrawner.budget

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

sealed class AsyncState<out T> {
    object Loading : AsyncState<Nothing>()
    class Success<T>(val data: T) : AsyncState<T>()
    class Error(val exception: Exception) : AsyncState<Nothing>() {
        constructor(message: String) : this(RuntimeException(message))
    }

    object Exit : AsyncState<Nothing>()
}

interface AsyncViewModel<T> {
    val state: MutableStateFlow<AsyncState<T>>
}


fun <VM, T> VM.load(block: suspend () -> T): Job where VM : ViewModel, VM : AsyncViewModel<T> = viewModelScope.launch {
    if (state.replayCache.firstOrNull() !is AsyncState.Success) {
        state.emit(AsyncState.Loading)
    }
    try {
        state.emit(AsyncState.Success(block()))
    } catch (e: Exception) {
        state.emit(AsyncState.Error(e))
        Log.e("AsyncViewModel", "Failed to load data", e)
    }
}

