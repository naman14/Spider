package com.naman14.spider.models

import com.naman14.spider.utils.Constants
import okhttp3.Response
import java.io.IOException
import java.nio.charset.UnsupportedCharsetException

class NetworkResponse(response: Response?) {

    var statusCode: Int = 0
    var isSuccessful: Boolean = false
    var responseString: String? = null
    var networkRequest: NetworkRequest? = null
    var responseReceivedAtNano: Long = 0

    init {
        response?.let {
            this.statusCode = response.code()
            this.isSuccessful = response.isSuccessful
            this.responseString = ""
            this.networkRequest = NetworkRequest(response.request())
            this.responseReceivedAtNano = System.nanoTime()
            parseRequestForResponseString(response)
        }
    }

    fun parseRequestForResponseString(response: Response) {
        val responseBody = response.body()
        try {
            val source = responseBody.source()
            source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.

            val buffer = source.buffer()

            var charset = Constants.UTF8
            val contentType = responseBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(Constants.UTF8)

            }
            this.responseString = buffer.clone().readString(charset)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: UnsupportedCharsetException) {
            e.printStackTrace()
        }

    }

}