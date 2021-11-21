package com.example.airqualitymonitoring

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class EchoWebSocketListener(private val webSocketCallback: WebSocketCallback) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response?) {
    }

    override fun onMessage(webSocket: WebSocket?, message: String) {
        webSocketCallback.onMessage(message)
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(CLOSE_STATUS, null)
        webSocketCallback.onClose("Closing Socket : $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket?, throwable: Throwable, response: Response?) {
        webSocketCallback.onError("Error : " + throwable.message)
    }

    companion object {
        private const val CLOSE_STATUS = 1000
    }
}