package com.wbrawner.budget.lib.network

import okhttp3.Interceptor
import okhttp3.Response

const val DEFAULT_URL = "http://localhost"

class BaseUrlHelper(var url: String = DEFAULT_URL) {
    val interceptor = object: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val newRequest = request.newBuilder()
            val url = request.url.toString().replace(DEFAULT_URL, url)
            return chain.proceed(newRequest.url(url).build())
        }
    }
}