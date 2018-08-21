package com.naman14.spider.utils

import java.nio.charset.Charset

class Constants {

    object DataSize {

        val BYTE: Long = 1

        val KB = 1000 * BYTE

        val MB = 1000 * KB
    }

    companion object {

        val NETWORK_INTERCEPTOR = "Spider"

        val UTF8 = Charset.forName("UTF-8")

        val NULL_STRING = "null"

        val HEADER = "Header"

        val PARAMETERS = "Parameters"

        val BODY = "Body"

        val METHOD = "Method"

        val URL = "Url"

        val STATUS = "Status"

        val NETWORK_LATENCY = "Latency"

        val CURL = "cURL"

        val MAX_INTENT_SIZE_MB = DataSize.MB
    }
}
