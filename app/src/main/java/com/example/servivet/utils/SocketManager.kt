package com.example.servivet.utils

import android.util.Log
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.EngineIOException
import java.net.URISyntaxException

object SocketManager {

    private var socket: Socket? = null

    init {
        val socketUrl = "http://13.235.137.221:3476"
       // val socketUrl ="https://c5e6-122-176-117-180.ngrok-free.app"
        //val socketUrl = "https://0eaf-59-144-166-73.ngrok-free.app"
        val token = Session.token
        try {
            val options = IO.Options().apply {
                auth = mapOf("token" to token)
            }
            Log.e("TAG", "socketDAta:${socketUrl} ")
            Log.e("TAG", "socketDAta:${options} ")

            socket = IO.socket(socketUrl, options)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun connect() {
        socket?.connect()
    }

    fun getSocket(): Socket {
        return socket!!
    }

    fun disconnect() {
        socket?.disconnect()
    }


}