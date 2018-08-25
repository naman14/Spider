package com.naman14.spider.app.data

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.naman14.spider.SpiderInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DataHandler private constructor(context: Context) {

    val apiService: ApiService

    companion object {

        @Volatile private var INSTANCE: DataHandler? = null

        const val BASE_URL = "https://jsonplaceholder.typicode.com/"

        fun getInstance(context: Context): DataHandler? =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildInstance(context).also { INSTANCE = it }
                }

        private fun buildInstance(context: Context): DataHandler {
           return DataHandler(context)
        }
    }

    init {

        val interceptor = SpiderInterceptor.getInstance(context)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        apiService = retrofit.create(ApiService::class.java)
    }
}