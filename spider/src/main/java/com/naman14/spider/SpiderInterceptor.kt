package com.naman14.spider

import okhttp3.Interceptor
import okhttp3.Response

class SpiderInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalResponse = chain.proceed(originalRequest)

        return originalResponse
    }
}