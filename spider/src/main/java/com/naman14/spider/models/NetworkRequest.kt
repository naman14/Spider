package com.naman14.spider.models

import android.net.Uri
import com.naman14.spider.utils.Constants
import okhttp3.FormBody
import okhttp3.MultipartBody
import okhttp3.Request
import okio.Buffer
import java.io.IOException
import java.nio.charset.UnsupportedCharsetException
import java.util.HashMap

class NetworkRequest(request: Request?) {

    lateinit var method: String
    lateinit var uri: Uri
    lateinit var headerMap: MutableMap<String, String>
    lateinit var bodyMap: MutableMap<String, String>
    lateinit var requestString: String
    var requestSentAtNano: Long = 0

    init {
        request?.let {
            this.method = request.method()
            this.uri = Uri.parse(request.url().toString())
            this.headerMap = HashMap()
            this.bodyMap = HashMap()
            this.requestString = ""
            this.requestSentAtNano = System.nanoTime()
            parseRequestForHeaderMap(request)
            parseRequestForBodyMap(request)
            parseRequestForRequestString(request)
        }
    }

    private fun parseRequestForHeaderMap(request: Request) {
        if (request.headers() != null) {
            val headers = request.headers()
            for (headerKey in headers.names()) {
                this.headerMap[headerKey] = headers.get(headerKey)
            }
        }
    }

    private fun parseRequestForBodyMap(request: Request) {
        val requestBody = request.body()
        if (requestBody is FormBody) {
            for (counter in 0 until requestBody.size()) {
                val bodyKey = requestBody.encodedName(counter)
                val bodyValue = requestBody.encodedValue(counter)
                this.bodyMap[bodyKey] = bodyValue
            }
        } else if (requestBody is MultipartBody) {
            var counter = 0
            for (multiBodyPart in requestBody.parts()) {
                this.bodyMap[counter.toString()] = multiBodyPart.toString()
                counter++
            }
        }
    }

    private fun parseRequestForRequestString(request: Request) {
        val requestBody = request.body()
        if (requestBody != null) {
            try {
                val buffer = Buffer()
                requestBody.writeTo(buffer)

                var charset = Constants.UTF8
                val contentType = requestBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(Constants.UTF8)
                }

                this.requestString = buffer.clone().readString(charset)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: UnsupportedCharsetException) {
                e.printStackTrace()
            }

        }
    }

    fun getRequestPath(): String {
        return uri.path
    }


}