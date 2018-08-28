package com.naman14.spider

import android.content.Context
import com.google.gson.Gson
import com.naman14.spider.db.RequestsDao
import com.naman14.spider.db.SpiderDatabase
import com.naman14.spider.models.NetworkCall
import com.naman14.spider.server.ClientServer
import java.nio.charset.Charset
import com.google.gson.reflect.TypeToken
import com.naman14.spider.models.NetworkResponse
import okhttp3.*

class SpiderInterceptor: Interceptor {

    private lateinit var memoryDb: RequestsDao
    private lateinit var diskDb: RequestsDao
    private lateinit var server: ClientServer

    companion object {
        @Volatile private var INSTANCE: SpiderInterceptor? = null

        fun getInstance(context: Context): SpiderInterceptor? =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildInstance(context).also { INSTANCE = it }
                }

        private fun buildInstance(context: Context): SpiderInterceptor {
            val interceptor = SpiderInterceptor()
            interceptor.memoryDb = SpiderDatabase.getMemoryInstance(context)!!.requestsDao()
            interceptor.diskDb = SpiderDatabase.getDiskInstance(context)!!.requestsDao()
            interceptor.server = ClientServer(context)
            return interceptor
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        var networkCall = NetworkCall(originalRequest, null)

        if (shouldOverride(networkCall)) {

            networkCall = overrideCall(networkCall)

            val modifiedRequest: Request.Builder = originalRequest.newBuilder()
            val modifiedResponse: Response.Builder = Response.Builder()

            //modify request headers
            for (k in networkCall.networkRequest.headerMap.keys) {
                modifiedRequest.addHeader(k, networkCall.networkRequest.headerMap[k])
            }

            originalRequest.body()?.let {
                val requestBody = originalRequest.body()
                var modifiedRequestBody: RequestBody? = null

                when (requestBody.contentType().subtype()) {
                    "json" -> {
                        modifiedRequestBody = RequestBody.create(requestBody.contentType(),
                                networkCall.networkRequest.requestString)
                    }
                    "form" -> {
                        //modify request body map
                        val formBody = FormBody.Builder()
                        for (k in networkCall.networkRequest.bodyMap.keys) {
                            formBody.add(k, networkCall.networkRequest.bodyMap[k])
                        }
                        modifiedRequestBody = RequestBody.create(requestBody.contentType(),
                                networkCall.networkRequest.bodyMap.toJsonString())
                    }
                }

                modifiedRequestBody?.let {
                    modifiedRequest.post(modifiedRequestBody)
                }
            }

            val modifiedCall = NetworkCall(modifiedRequest.build(), null)
            modifiedCall.isModified = networkCall.isModified
            memoryDb.insertRequest(modifiedCall.toRequestEntity())

            networkCall.networkResponse?.headerMap?.let {
                for (k in it.keys) {
                    modifiedResponse.addHeader(k, it[k])
                }
            }
            //modify response only if a custom response body is set
            networkCall.networkResponse?.responseString?.let {
                modifiedResponse.request(modifiedRequest.build())
                modifiedResponse.protocol(Protocol.HTTP_1_0)

                //always return 200 if custom response body
                modifiedResponse.code(200)
                modifiedResponse.body(ResponseBody.create(MediaType.parse("application/json"),
                   it.toByteArray(Charset.forName("UTF-8"))))

                modifiedCall.networkResponse = NetworkResponse(modifiedResponse.build())
                memoryDb.insertRequest(modifiedCall.toRequestEntity())

                return modifiedResponse.build()
            }

            val response = chain.proceed(modifiedRequest.build())
            modifiedCall.networkResponse = NetworkResponse(response)
            memoryDb.insertRequest(modifiedCall.toRequestEntity())
            return response

        } else {
            memoryDb.insertRequest(networkCall.toRequestEntity())
            val originalResponse = chain.proceed(originalRequest)
            networkCall.networkResponse = NetworkResponse(originalResponse)
            memoryDb.insertRequest(networkCall.toRequestEntity())
            return originalResponse
        }

    }

    private fun shouldOverride(originalCall: NetworkCall): Boolean {
        return diskDb.getRequestSync(originalCall.id).isNotEmpty()
    }

    private fun overrideCall(originalCall: NetworkCall): NetworkCall {
        val savedCall = diskDb.getRequestSync(originalCall.id)
        if (savedCall.isNotEmpty()) {
            savedCall[0].let {
                it.responseString?.let {
                    originalCall.networkResponse?.responseString = it
                }
                it.requestString?.let {
                    originalCall.networkRequest.requestString = it
                }
                it.requestBodyMap?.let {
                    val type = object : TypeToken<MutableMap<String, String>>() {}.type
                    val bodyMap: MutableMap<String, String> = Gson().fromJson(it, type)
                    originalCall.networkRequest.bodyMap = bodyMap
                }
                it.requestHeaders?.let {
                    val type = object : TypeToken<MutableMap<String, String>>() {}.type
                    val headerMap: MutableMap<String, String> = Gson().fromJson(it, type)
                    originalCall.networkRequest.headerMap = headerMap
                }
                it.responseHeaders?.let {
                    val type = object : TypeToken<MutableMap<String, String>>() {}.type
                    val headerMap: MutableMap<String, String> = Gson().fromJson(it, type)
                    originalCall.networkResponse?.headerMap = headerMap
                }
                originalCall.isModified = true
            }

        }
        return originalCall
    }
}