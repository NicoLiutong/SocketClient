package com.nico.socketclient.post

interface IPoster {
    fun enqueue(data: ByteArray)
}