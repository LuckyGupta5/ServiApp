package com.example.servivet.utils

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.EngineIOException
import java.net.URISyntaxException

object SocketManager {

    private var socket: Socket? = null

    init {
        try {
            val options = IO.Options().apply {
            }
            socket = IO.socket("http://13.235.137.221:3476", options)
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

    fun addConnectListener(listener: Emitter.Listener) {
        socket?.on(Socket.EVENT_CONNECT, listener)
    }

    fun addDisconnectListener(listener: Emitter.Listener) {
        socket?.on(Socket.EVENT_DISCONNECT, listener)
    }

    // Add other event listeners and methods as needed
}