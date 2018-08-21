package com.naman14.spider.models

import okhttp3.Request
import okhttp3.Response

class NetworkCall(id: String, request: Request, response: Response) {

    private var id: String? = null
    @Transient
    private var request: Request
    private var networkRequest: NetworkRequest
    private var networkResponse: NetworkResponse? = null

    init {
        this.id = id
        this.request = request
        this.networkRequest = NetworkRequest(request)
        this.networkResponse = NetworkResponse(response)
    }

    fun generateId(request: Request): String {
        return request.url().url().toString()
    }

    fun getFormattedNetworkCallTime(): String {
        networkResponse?.let { response ->
            val networkCallTime = (response.responseReceivedAtNano - networkRequest.requestSentAtNano) / 1e3
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