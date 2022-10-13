package com.nico.socketclient.meta

class SocketOption(
    val mHeartBeatConfig: HeartBeatConfig?,
    val mHead: ByteArray?,
    val mTail: ByteArray?
) {
    private constructor(builder: Builder) : this(builder.mHeartBeatConfig, builder.mHead, builder.mTail)

    class Builder {
        var mHeartBeatConfig: HeartBeatConfig? = null
            private set

        var mHead: ByteArray? = null
            private set

        var mTail: ByteArray? = null
            private set

        fun setHeartBeat(data: ByteArray, interval: Long) = apply { this.mHeartBeatConfig = HeartBeatConfig(data, interval) }

        fun setHead(head: ByteArray) = apply { this.mHead = head }

        fun setTail(tail: ByteArray) = apply { this.mTail = tail }

        fun build() = SocketOption(this)
    }
    class HeartBeatConfig(var data: ByteArray?, var interval: Long)
}