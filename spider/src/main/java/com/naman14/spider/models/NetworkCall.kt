package com.naman14.spider.models

import okhttp3.Request
import okhttp3.Response

class NetworkCall(request: Request, response: Response?) {

    var id: String
    @Transient
    var request: Request
    var networkRequest: NetworkRequest
    var networkResponse: NetworkResponse? = null
    var isModified: Boolean = false

    init {
        this.id = generateId(request)
        this.request = request
        this.networkRequest = NetworkRequest(request)
        this.networkResponse = NetworkResponse(response)
    }

    fun generateId(request: Request): String {
        return request.url().url().toString()
    }

    fun generateUniqueId(request: Request): String {
        return generateId(request) + System.nanoTime()
    }

    fun getRequestPath(): String {
        return networkRequest.getRequestPath()
    }

    fun getFormattedNetworkCallTime(): String {
        networkResponse?.let { response ->
            val networkCallTime = (response.responseReceivedAt - networkRequest.requestSentAt) / 1e3
            return String.format("%.1fms", networkCallTime)
        }
        return ""
    }

    fun isResponseReceived(): Boolean {
        return networkResponse != null
    }

    override fun equals(other: Any?): Boolean {
        return other is NetworkCall && request == other.request
    }

}