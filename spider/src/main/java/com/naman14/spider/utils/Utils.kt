package com.naman14.spider.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.content.res.AssetManager
import android.text.TextUtils
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import android.app.ActivityManager

object Utils {

    fun getAddressLog(context: Context, port: Int): String {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress = wifiManager.connectionInfo.ipAddress
        @SuppressLint("DefaultLocale")
        val formattedIpAddress = String.format("%d.%d.%d.%d",
                ipAddress and 0xff,
                ipAddress shr 8 and 0xff,
                ipAddress shr 16 and 0xff,
                ipAddress shr 24 and 0xff)
        return "$formattedIpAddress:$port"
    }

    fun detectMimeType(fileName: String): String? {
        return if (TextUtils.isEmpty(fileName)) {
            null
        } else if (fileName.endsWith(".html")) {
            "text/html"
        } else if (fileName.endsWith(".js")) {
            "application/javascript"
        } else if (fileName.endsWith(".css")) {
            "text/css"
        } else {
            "application/octet-stream"
        }
    }

    @Throws(IOException::class)
    fun loadContent(fileName: String, assetManager: AssetManager): ByteArray? {
        var input: InputStream? = null
        try {
            val output = ByteArrayOutputStream()
            input = assetManager.open(fileName)
            val buffer = ByteArray(1024)
            var size: Int = input!!.read(buffer)
            while (-1 != size) {
                output.write(buffer, 0, size)
                size = input.read(buffer)
            }
            output.flush()
            return output.toByteArray()
        } catch (e: FileNotFoundException) {
            return null
        } finally {
            try {
                if (null != input) {
                    input!!.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun getPackageName(context: Context): String {
        val mActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return mActivityManager.runningAppProcesses[0].processName
    }
}