package com.nico.socketclient.post

import com.nico.socketclient.SocketClient
import java.util.concurrent.Executor

class SyncPoster(private val mSocketClient: SocketClient, private val mExecutor: Executor) : Runnable, IPoster {
    private val queue: PendingPostQueue = PendingPostQueue()

    @Volatile private var executorRunning: Boolean = false

    override fun enqueue(data: ByteArray) {
        val pendingPost = PendingPost.obtainPendingPost(data)
        synchronized(this) {
            queue.enqueue(pendingPost)
            if (!executorRunning) {
                executorRunning = true
                mExecutor.execute(this)
            }
        }
    }

    override fun run() {
        try {
            try {
                while (true) {
                    var pendingPost = queue.poll(1000)
                    if (pendingPost == null) {
                        synchronized(this) {
                            pendingPost = queue.poll()
                            if (pendingPost == null) {
                                executorRunning = false
                                return
                            }
                        }
                    }
                    pendingPost?.let {
                        mSocketClient.mSocket.getOutputStream()?.apply {
                            try {
                                write(pendingPost!!.data)
                                flush()
                            } catch (e: Exception) {
                                mSocketClient.disconnect()
                            }
                            PendingPost.releasePendingPost(pendingPost!!)
                        }
                    }
                }
            } catch (e: InterruptedException) {
                e.toString()
            }

        } finally {
            executorRunning = false
        }
    }
}