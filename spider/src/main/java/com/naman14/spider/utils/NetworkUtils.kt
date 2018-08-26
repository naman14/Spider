package com.naman14.spider.utils

import android.text.TextUtils
import com.naman14.spider.models.NetworkCall
import com.naman14.spider.models.NetworkRequest

object NetworkUtils {

    fun getCurlRequest(networkCall: NetworkCall): String {
        val request = networkCall.networkRequest
        val curlStringBuilder = StringBuilder()

        curlStringBuilder.append("curl")
        curlStringBuilder.append(" ")
        curlStringBuilder.append("-X ")
        curlStringBuilder.append(request.method)
        addHeadersToCurlStringBuilder(curlStringBuilder, request)
        addBodyToCurlStringBuilder(curlStringBuilder, request)
        addUrlToCurlStringBuilder(curlStringBuilder, request)

        return curlStringBuilder.toString()
    }

    private fun addHeadersToCurlStringBuilder(curlStringBuilder: StringBuilder, request: NetworkRequest) {
        val headerMap = request.headerMap
        val headerKeySet = headerMap.keys
        if (headerKeySet != null) {
            for (headerKey in headerKeySet) {
                curlStringBuilder.append(" ")
                curlStringBuilder.append("-H")
                curlStringBuilder.append(" ")
                curlStringBuilder.append("\'")
                curlStringBuilder.append(headerKey)
                curlStringBuilder.append(":")
                curlStringBuilder.append(" ")
                curlStringBuilder.append(headerMap.get(headerKey))
                curlStringBuilder.append("\'")
            }
        }
    }

    private fun addBodyToCurlStringBuilder(curlStringBuilder: StringBuilder, request: NetworkRequest) {
        if (!TextUtils.isEmpty(request.requestString)) {
            curlStringBuilder.append(" ")
            curlStringBuilder.append("-d")
            curlStringBuilder.append(" ")
            curlStringBuilder.append("\'")
            curlStringBuilder.append(request.requestString)
            curlStringBuilder.append("\'")

        }

    }

    private fun addUrlToCurlStringBuilder(curlStringBuilder: StringBuilder, request: NetworkRequest) {
        curlStringBuilder.append(" ")
        curlStringBuilder.append("\'")
        curlStringBuilder.append(request.uri.toString())
        curlStringBuilder.append("\'")
    }

}
