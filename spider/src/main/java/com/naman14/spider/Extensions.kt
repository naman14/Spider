package com.naman14.spider

import com.google.gson.Gson

fun MutableMap<String, String>.toJsonString(): String? {
    return Gson().toJson(this)
}