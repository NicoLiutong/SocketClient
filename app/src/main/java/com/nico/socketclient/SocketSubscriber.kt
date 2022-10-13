package com.nico.socketclient

import com.nico.socketclient.meta.DataWrapper
import com.nico.socketclient.meta.SocketState
import io.reactivex.functions.Consumer

abstract class SocketSubscriber : Consumer<DataWrapper>{

    override fun accept(t: DataWrapper) {
        when (t.state) {
            SocketState.GETDATA -> onResponse(t.data)
            SocketState.CONNECTED -> onConnected()
            SocketState.DISCONNECTED -> onDisconnected()
            SocketState.FAILCONNECTED -> onFailConnected()
        }
    }

    abstract fun onFailConnected()

    abstract fun onConnected()

    abstract fun onDisconnected()

    abstract fun onResponse(data: ByteArray)
}