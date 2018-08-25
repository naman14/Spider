package com.naman14.spider.server

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.koushikdutta.async.http.server.AsyncHttpServer
import com.koushikdutta.async.callback.DataCallback
import com.koushikdutta.async.http.WebSocket
import com.koushikdutta.async.callback.CompletedCallback
import com.koushikdutta.async.http.server.HttpServerRequestCallback
import com.naman14.spider.db.RequestEntity
import com.naman14.spider.db.RequestsDao
import com.naman14.spider.db.SpiderDatabase
import com.naman14.spider.models.NetworkCall
import com.naman14.spider.sendToAll
import com.naman14.spider.toJSONString
import com.naman14.spider.toRequestEntity
import com.naman14.spider.utils.Utils
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer

class ClientServer(context: Context): LifecycleOwner {

    private lateinit var websocketServer: AsyncHttpServer
    private lateinit var httpServer: AsyncHttpServer
    private lateinit var httpCallback: HttpServerRequestCallback
    private lateinit var websocketCallback: AsyncHttpServer.WebSocketRequestCallback
    private var memoryDb: RequestsDao = SpiderDatabase.getMemoryInstance(context)!!.requestsDao()
    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    private val socketList = ArrayList<WebSocket>()

    init {
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
        startServer(context)
    }

    private fun startServer(context: Context) {

        initRequestHandler(context)

        websocketServer = AsyncHttpServer()
        websocketServer.websocket("/", null, websocketCallback)
        websocketServer.listen(6061)

        httpServer = AsyncHttpServer()
        httpServer.get(".*.", httpCallback)
        httpServer.listen(6060)
    }

    private fun initRequestHandler(context: Context) {

        httpCallback = HttpServerRequestCallback { request, response ->
            var route = request.path.substring(1)
            if (route == "") route = "index.html"
            response.send(Utils.detectMimeType(route), Utils.loadContent(route, context.assets))

        }

        websocketCallback = AsyncHttpServer.WebSocketRequestCallback { webSocket, request ->
            socketList.add(webSocket)

            webSocket.closedCallback = CompletedCallback { ex ->
                try {
                    ex?.printStackTrace()
                } finally {
                    socketList.remove(webSocket)
                }
            }

            webSocket.stringCallback = WebSocket.StringCallback { s ->
                try {
                    handleMessage(s, webSocket)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            webSocket.dataCallback = DataCallback { dataEmitter, byteBufferList -> byteBufferList.recycle() }

            val liveData = memoryDb.getAllRequests()
            Handler(Looper.getMainLooper()).post({
                liveData.observeForever(object: Observer<List<RequestEntity>> {
                    override fun onChanged(it: List<RequestEntity>) {
                        sendRequests(it)
                    }
                })
            })

        }
    }

    private fun handleMessage(message: String, socket: WebSocket) {

    }

    private fun sendResponse(route: String) {

    }

    fun sendRequests(requests: List<RequestEntity>) {
        socketList.sendToAll(requests.toJSONString())
    }

    fun sendNetworkCall(networkCall: NetworkCall) {
        socketList.sendToAll(networkCall.toRequestEntity().toJSONString())
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

}