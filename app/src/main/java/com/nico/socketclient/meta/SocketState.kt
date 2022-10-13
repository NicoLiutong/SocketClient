package com.nico.socketclient.meta

object SocketState {
    const val OPEN = 0x00

    const val CONNECTING = 0x01

    const val CLOSE = 0x02
}

object ThreadStrategy {

    const val SYNC = 0x00

    const val ASYNC = 0x01
}