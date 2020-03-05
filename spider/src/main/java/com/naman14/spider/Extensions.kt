package com.naman14.spider

import com.google.gson.Gson
import com.koushikdutta.async.http.WebSocket
import com.naman14.spider.db.RequestEntity
import com.naman14.spider.models.NetworkCall
import com.naman14.spider.utils.NetworkUtils

fun MutableMap<String, String>.toJsonString(): String? {
    return Gson().toJson(this)
}

fun RequestEntity.toJSONString(): String {
    return Gson().toJson(this)
}

fun List<RequestEntity>.toJSONString(): String {
    return Gson().toJson(this)
}

fun NetworkCall.toRequestEntity(): RequestEntity {
    val successful = networkResponse != null
    return RequestEntity(id,
            networkRequest.getRequestPath(),
            networkRequest.headerMap.toJsonString(),
            networkRequest.bodyMap.toJsonString(),
            networkRequest.requestString,
            networkRequest.method,
            networkRequest.requestSentAt,
            networkResponse?.statusCode,
            networkResponse?.responseString,
            networkResponse?.headerMap?.toJsonString(),
            networkResponse?.responseReceivedAt,
            successful,
            NetworkUtils.getCurlRequest(this),
            this.isModified)
}

fun List<WebSocket>.sendToAll(data: String) {
    val iter = iterator()
    while (iter.hasNext()) {
        iter.next().send(data)
    }
}

