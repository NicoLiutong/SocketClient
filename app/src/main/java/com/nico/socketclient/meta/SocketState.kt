package com.nico.socketclient.meta

object SocketState {

    const val CONNECTED = 0x00

    const val GETDATA = 0x01

    const val DISCONNECTED = 0x02

    const val FAILCONNECTED = 0x03
}

object ThreadStrategy {

    const val SYNC = 0x00

    const val ASYNC = 0x01
}