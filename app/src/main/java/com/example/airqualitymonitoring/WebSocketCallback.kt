package com.example.airqualitymonitoring

interface WebSocketCallback {
    fun onMessage(message: String)
    fun onClose(message: String)
    fun onError(error: String)
}