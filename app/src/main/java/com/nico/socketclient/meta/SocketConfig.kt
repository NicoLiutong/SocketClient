package com.nico.socketclient.meta

import java.nio.charset.Charset

class SocketConfig(
    val mIp: String?,
    val mPort: Int?,
    val mTimeout: Int?,
    val mCharset: Charset = Charsets.UTF_8,
    val mThreadStrategy: Int?
) {
    private constructor(builder: Builder) : this(builder.mIp, builder.mPort,
        builder.mTimeout, builder.mCharset, builder.mThreadStrategy)

    class Builder {
        var mIp: String? = null
            private set

        var mPort: Int? = null
            private set

        var mTimeout: Int? = null
            private set

        var mCharset: Charset = Charsets.UTF_8
            private set

        var mThreadStrategy: Int? = ThreadStrategy.ASYNC

        fun setIp(ip: String) = apply { this.mIp = ip }

        fun setPort(port: Int) = apply { this.mPort = port }

        fun setTimeout(timeout: Int) = apply { this.mTimeout = timeout }

        fun setCharset(charset: Charset) = apply { this.mCharset = charset}

        fun setThreadStrategy(threadStrategy: Int) = apply { this.mThreadStrategy = threadStrategy }

        fun build() = SocketConfig(this)
    }
}