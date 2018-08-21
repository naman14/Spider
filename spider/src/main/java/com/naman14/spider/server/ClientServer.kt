package com.naman14.spider.server

import android.content.Context
import android.util.Log

import java.io.IOException
import java.net.ServerSocket
import java.net.SocketException

class ClientServer(context: Context, private val mPort: Int) : Runnable {

    private val mRequestHandler: RequestHandler
    private var isRunning: Boolean = false
    private var mServerSocket: ServerSocket? = null

    init {
        mRequestHandler = RequestHandler(context)
    }

    fun start() {
        isRunning = true
        Thread(this).start()
    }

    fun stop() {
        try {
            isRunning = false
            if (null != mServerSocket) {
                mServerSocket!!.close()
                mServerSocket = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error closing the server socket.", e)
        }

    }

    override fun run() {
        try {
            mServerSocket = ServerSocket(mPort)
            while (isRunning) {
                val socket = mServerSocket!!.accept()
                mRequestHandler.handle(socket)
                socket.close()
            }
        } catch (e: SocketException) {
            // The server was stopped; ignore.
        } catch (e: IOException) {
            Log.e(TAG, "Web server error.", e)
        } catch (ignore: Exception) {
            Log.e(TAG, "Exception.", ignore)
        }

    }

    companion object {
        private val TAG = "ClientServer"
    }
}