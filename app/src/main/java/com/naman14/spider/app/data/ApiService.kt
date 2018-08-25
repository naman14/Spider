package com.naman14.spider.app.data

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("todos/1")
    fun getTodos(): Call<Any>

    @GET("todos/2")
    fun getPosts(): Call<Any>


}