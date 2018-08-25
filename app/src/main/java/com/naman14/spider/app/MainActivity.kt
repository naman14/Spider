package com.naman14.spider.app

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import com.naman14.spider.app.data.DataHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : Activity(), Callback<Any> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DataHandler.getInstance(this)?.apiService?.getTodos()?.enqueue(this)

        Handler().postDelayed( {
            DataHandler.getInstance(this)?.apiService?.getPosts()?.enqueue(this)
        }, 5000)

    }

    override fun onResponse(call: Call<Any>?, response: Response<Any>?) {

    }


    override fun onFailure(call: Call<Any>?, t: Throwable?) {

    }
}
