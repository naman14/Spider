package com.naman14.spider

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class SpiderInterceptor: Interceptor {

    companion object {
        fun getInstance(context: Context): SpiderInterceptor? = null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }

}