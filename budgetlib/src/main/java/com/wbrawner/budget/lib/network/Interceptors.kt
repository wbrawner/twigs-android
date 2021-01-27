package com.wbrawner.budget.lib.network

import android.content.SharedPreferences
import com.wbrawner.budget.common.PREF_KEY_TOKEN
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

const val DEFAULT_URL = "http://localhost"

class BaseUrlHelper(var url: String = DEFAULT_URL) {
    val interceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val newRequest = request.newBuilder()
            val url = request.url.toString().replace(DEFAULT_URL, url)
            return chain.proceed(newRequest.url(url).build())
        }
    }
}

class AuthHelper @Inject constructor(val sharedPreferences: SharedPreferences) {
    val interceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val token = sharedPreferences.getString(PREF_KEY_TOKEN, null)
                    ?: return chain.proceed(chain.request())
            val newRequest = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            return chain.proceed(newRequest)
        }
    }
}