package com.nico.socketclient.post

import com.nico.socketclient.SocketClient
import java.util.concurrent.Executor

class AsyncPoster(private val mSocketClient: SocketClient, private val mExecutor: Executor) : Runnable, IPoster {
    private val queue: PendingPostQueue = PendingPostQueue()

    override fun enqueue(data: ByteArray) {
        val pendingPost = PendingPost.obtainPendingPost(data)
        queue.enqueue(pendingPost)
        mExecutor.execute(this)
    }

    override fun run() {
        val pendingPost = queue.poll() ?: throw IllegalStateException("No pending post available")
        mSocketClient.mSocket.takeIf { it.isConnected }?.getOutputStream()?.apply {
            try {
                write(pendingPost.data)
                flush()
            } catch (e: Exception) {
                mSocketClient.disconnect()
            }
            PendingPost.releasePendingPost(pendingPost)
        }
    }
}