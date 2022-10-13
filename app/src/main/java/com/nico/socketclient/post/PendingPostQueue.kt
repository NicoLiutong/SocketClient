package com.nico.socketclient.post

class PendingPostQueue {
    private var head: PendingPost? = null
    private var tail: PendingPost? = null
    private val lock = java.lang.Object()


    fun enqueue(pendingPost: PendingPost) = synchronized(lock) {
        if (tail != null) {
            tail!!.next = pendingPost
            tail = pendingPost
        } else if (head == null) {
            tail = pendingPost
            head = tail
        } else {
            throw IllegalStateException("Head present, but no tail")
        }
        lock.notifyAll()
    }

    @Synchronized fun poll(): PendingPost? {
        val pendingPost = head
        head?.let {
            head = head!!.next
            if (head == null) {
                tail = null
            }
        }
        return pendingPost
    }

    @Throws(InterruptedException::class)
    fun poll(maxMillisToWait: Int): PendingPost? = synchronized(lock){
        if (head == null) {
            lock.wait(maxMillisToWait.toLong())
        }
        return poll()
    }
}