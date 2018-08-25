package com.naman14.spider

import com.google.gson.Gson
import com.koushikdutta.async.http.WebSocket
import com.naman14.spider.db.RequestEntity
import com.naman14.spider.models.NetworkCall

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
    return RequestEntity(id,
            networkRequest.headerMap.toJsonString(),
            networkRequest.bodyMap.toJsonString(),
            networkRequest.method,
            networkRequest.requestSentAtNano,
            networkResponse?.statusCode,
            networkResponse?.responseString,
            networkResponse?.responseReceivedAtNano)
}

fun List<WebSocket>.sendToAll(data: String) {
    val iter = iterator()
    while (iter.hasNext()) {
        iter.next().send(data)
    }
}

