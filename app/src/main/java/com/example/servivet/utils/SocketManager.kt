package com.example.servivet.utils

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketManager {
    private var socket: Socket? = null

    fun initializeSocket(token: String) {
      val socketUrl = "http://13.235.137.221:3476"// development new url
      // val socketUrl = "https://795e-59-144-166-73.ngrok-free.app/"//  local url
//        val socketUrl = "http://13.126.60.236:4242"// staging
        Log.e("TAG", "initializeSocketToken: ${token}", )

        try {
            val options = IO.Options().apply {
                auth = mapOf("token" to token)
            }
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


    fun isConnected(): Boolean {
        return socket != null && socket!!.connected()
    }


    fun reset() {
        disconnect()
        socket = null
        Session.token = null
    }

}