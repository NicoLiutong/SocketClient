package com.nico.socketclient

import com.nico.socketclient.meta.DataWrapper
import com.nico.socketclient.meta.SocketState
import io.reactivex.functions.Consumer

abstract class SocketSubscriber : Consumer<DataWrapper>{

    override fun accept(t: DataWrapper) {
        when (t.state) {
            SocketState.CONNECTING -> onResponse(t.data)
            SocketState.OPEN -> onConnected()
            SocketState.CLOSE -> onDisconnected()
        }
    }

    abstract fun onConnected()

    abstract fun onDisconnected()

    abstract fun onResponse(data: ByteArray)
}