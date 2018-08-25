package com.naman14.spider

import android.content.Context
import android.util.Log
import com.naman14.spider.db.RequestsDao
import com.naman14.spider.db.SpiderDatabase
import com.naman14.spider.models.NetworkCall
import com.naman14.spider.models.NetworkResponse
import com.naman14.spider.server.ClientServer
import com.naman14.spider.utils.Utils
import okhttp3.Interceptor
import okhttp3.Response

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
            Log.d("Spider", Utils.getAddressLog(context, 6060))
            return interceptor
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        var networkCall = NetworkCall(originalRequest, null)
        networkCall = overrideCall(networkCall)

        memoryDb.insertRequest(networkCall.toRequestEntity())
        val originalResponse = chain.proceed(originalRequest)
        memoryDb.insertRequest(networkCall.toRequestEntity())

        return originalResponse
    }

    private fun overrideCall(originalCall: NetworkCall): NetworkCall {
        if (diskDb.getRequestSync(originalCall.id).isNotEmpty()) {

        }
        return originalCall
    }
}