package com.nico.socketclient.post

class PendingPost private constructor(
    var data: ByteArray?,
    var next: PendingPost?)
{
    companion object {
        private val pendingPostPool = mutableListOf<PendingPost>()

        fun obtainPendingPost(data: ByteArray): PendingPost {
            synchronized(pendingPostPool) {
                val size = pendingPostPool.size
                if (size > 0) {
                    val pendingPost = pendingPostPool.removeAt(size - 1)
                    pendingPost.data = data
                    pendingPost.next = null
                    return pendingPost
                }
            }
            return PendingPost(data, null)
        }

        fun releasePendingPost(pendingPost: PendingPost) {
            pendingPost.data = null
            pendingPost.next = null
            synchronized(pendingPostPool) {
                if (pendingPostPool.size < 10000) {
                    pendingPostPool.add(pendingPost)
                }
            }
        }
    }
}