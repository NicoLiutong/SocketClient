package com.nico.socketclient

import com.nico.socketclient.meta.SocketConfig

class CreatSocketClient {
    companion object {
        @JvmStatic fun create(config: SocketConfig) : SocketClient = SocketClient(config)
    }
}