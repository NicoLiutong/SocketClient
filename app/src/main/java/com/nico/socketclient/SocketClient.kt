package com.nico.socketclient

import com.nico.socketclient.meta.DataWrapper
import com.nico.socketclient.meta.SocketConfig
import com.nico.socketclient.meta.SocketOption
import com.nico.socketclient.meta.ThreadStrategy
import com.nico.socketclient.post.AsyncPoster
import com.nico.socketclient.post.IPoster
import com.nico.socketclient.post.SyncPoster
import java.net.Socket
import io.reactivex.Observable
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SocketClient (val mConfig: SocketConfig) {

    lateinit var mSocket: Socket
    var mOption: SocketOption? = null
    lateinit var mObservable: Observable<DataWrapper>
    lateinit var mIPoster: IPoster
    var mExecutor: Executor = Executors.newCachedThreadPool()

    fun option(option: SocketOption): SocketClient {
        mOption = option
        return this
    }

    fun connect(): Observable<DataWrapper> {
        mSocket = Socket()
        mObservable = SocketObservable(mConfig, mSocket)
        mIPoster = if (mConfig.mThreadStrategy == ThreadStrategy.ASYNC) AsyncPoster(this, mExecutor) else SyncPoster(this, mExecutor)
        initHeartBeat()
        return mObservable
    }

    fun disconnect() {
        if (mObservable is SocketObservable) {
            (mObservable as SocketObservable).close()
        }
    }

    private fun initHeartBeat() {
        mOption?.apply {
            if (mHeartBeatConfig != null) {
                val disposable = Observable.interval(mHeartBeatConfig.interval, TimeUnit.MILLISECONDS)
                    .subscribe({
                        mIPoster.enqueue(mHeartBeatConfig.data?: ByteArray(0))
                    })
                if (mObservable is SocketObservable) {
                    (mObservable as SocketObservable).setHeartBeatRef(disposable)
                }
            }
        }
    }

    fun sendData(data: ByteArray) {
        mOption?.apply {
            if (mHead != null || mTail != null) {
                var result: String = data.toString()
                mHead?.let {
                    if (mHead.isNotEmpty()) {
                        mHead.toString().plus(result)
                    }
                }
                mTail?.let {
                    if (mTail.isNotEmpty()) {
                        result.plus(mTail.toString())
                    }
                }
                mIPoster.enqueue(result.toByteArray(charset = mConfig.mCharset))
                return@sendData
            }
        }
        mIPoster.enqueue(data)
    }

    fun sendData(string: String) {
        sendData(string.toByteArray(charset = mConfig.mCharset))
    }

    fun isConnecting(): Boolean {
        return mSocket.isConnected
    }


}