package com.naman14.spider.server

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.koushikdutta.async.http.server.AsyncHttpServer
import com.koushikdutta.async.callback.DataCallback
import com.koushikdutta.async.http.WebSocket
import com.koushikdutta.async.callback.CompletedCallback
import com.koushikdutta.async.http.server.HttpServerRequestCallback
import com.naman14.spider.db.RequestEntity
import com.naman14.spider.db.RequestsDao
import com.naman14.spider.db.SpiderDatabase
import com.naman14.spider.sendToAll
import com.naman14.spider.toJSONString
import com.naman14.spider.utils.Utils
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.koushikdutta.async.http.body.AsyncHttpRequestBody
import com.naman14.spider.R
import org.json.JSONObject

class ClientServer(context: Context) {

    private lateinit var websocketServer: AsyncHttpServer
    private lateinit var httpServer: AsyncHttpServer
    private lateinit var httpCallback: HttpServerRequestCallback
    private lateinit var websocketCallback: AsyncHttpServer.WebSocketRequestCallback
    private var memoryDb: RequestsDao = SpiderDatabase.getMemoryInstance(context)!!.requestsDao()
    private var diskDb: RequestsDao = SpiderDatabase.getDiskInstance(context)!!.requestsDao()

    private val socketList = ArrayList<WebSocket>()

    companion object {
        const val PORT = 6060
        const val CHANNEL_ID = "spider-monitor"
    }

    init {
        startServer(context)
    }

    private fun startServer(context: Context) {

        initRequestHandler(context)

        websocketServer = AsyncHttpServer()
        websocketServer.websocket("/", null, websocketCallback)
        websocketServer.listen(PORT + 1)

        httpServer = AsyncHttpServer()
        httpServer.get(".*.", httpCallback)
        httpServer.post(".*.", httpCallback)
        httpServer.listen(PORT)

        Log.d("Spider", "SPIDER:: Monitor network at  http://"+ Utils.getAddressLog(context, PORT) + " in your browser")
        showNotification(context)
    }

    private fun initRequestHandler(context: Context) {

        httpCallback = HttpServerRequestCallback { request, response ->
            var route = request.path.substring(1)
            val command = request.query.getString("command")
            if (command != null && command.isNotEmpty()) {
                when (command) {
                    "updateCall" -> {
                        val body: AsyncHttpRequestBody<JSONObject> = request.body as AsyncHttpRequestBody<JSONObject>
                        val type = object : TypeToken<RequestEntity>() {}.type
                        val requestEntity: RequestEntity = Gson().fromJson(body.get().toString(), type)
                        diskDb.insertRequest(requestEntity)
                        response.send("Success")
                    }
                    "resetCall" -> {
                        val body: AsyncHttpRequestBody<JSONObject> = request.body as AsyncHttpRequestBody<JSONObject>
                        val type = object : TypeToken<RequestEntity>() {}.type
                        val requestEntity: RequestEntity = Gson().fromJson(body.get().toString(), type)
                        diskDb.deleteRequest(requestEntity)
                        response.send("Success")
                    }
                    "getDeviceInfo" -> {
                        response.send(JSONObject().put("deviceName", Build.MODEL).put("packageName", Utils.getPackageName(context)))
                    }
                }
            } else {
                if (route == "") route = "index.html"
                response.send(Utils.detectMimeType(route), Utils.loadContent(route, context.assets))
            }
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
                    override fun onChanged(it: List<RequestEntity>?) {
                        it?.let {
                            sendRequests(it)
                        }
                    }
                })
            })
        }
    }

    private fun handleMessage(message: String, socket: WebSocket) {

    }

    private fun sendRequests(requests: List<RequestEntity>) {
        socketList.sendToAll(requests.toJSONString())
    }

    private fun showNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =  NotificationChannel(CHANNEL_ID, "Spider network monitoring",
                    NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://"+ Utils.getAddressLog(context, PORT)))
        builder.setSmallIcon(R.drawable.ic_notification)
        builder.setContentText("Monitor network at " + Utils.getAddressLog(context, PORT))
        builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, 0))
        builder.setContentTitle("Spider")
        notificationManager.notify(888, builder.build())
    }

}