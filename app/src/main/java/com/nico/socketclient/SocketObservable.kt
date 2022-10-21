package com.nico.socketclient


import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import com.nico.socketclient.meta.DataWrapper
import com.nico.socketclient.meta.SocketConfig
import com.nico.socketclient.meta.SocketState
import java.io.DataInputStream
import java.net.InetSocketAddress
import java.io.IOException
import java.net.Socket

class SocketObservable(val mConfig: SocketConfig, val mSocket: Socket) : Observable<DataWrapper>() {

    val mReadThread: ReadThread = ReadThread()
    lateinit var observerWrapper: SocketObserver
    var mHeartBeatRef: Disposable? = null

    override fun subscribeActual(observer: Observer<in DataWrapper>?) {
        observerWrapper = SocketObserver(observer)
        observer?.onSubscribe(observerWrapper)

        Thread(Runnable {
            try {
                mSocket.connect(InetSocketAddress(mConfig.mIp, mConfig.mPort ?: 1080), mConfig.mTimeout ?: 0)
                observer?.onNext(DataWrapper(SocketState.CONNECTED, ByteArray(0)))
                mReadThread.start()
            } catch (e: IOException) {
                println(e.toString())
                observer?.onNext(DataWrapper(SocketState.FAILCONNECTED, ByteArray(0)))
            }
        }).start()
    }

    fun setHeartBeatRef(ref: Disposable) {
        mHeartBeatRef = ref
    }

    fun close() {
        observerWrapper.dispose()
    }

    inner class SocketObserver(private val observer: Observer<in DataWrapper>?) : Disposable {

        fun onNext(data: ByteArray) {
            if (mSocket.isConnected) {
                observer?.onNext(DataWrapper(SocketState.GETDATA, data))
            }
        }

        fun onNext(dataWrapper: DataWrapper) {
            if (mSocket.isConnected) {
                observer?.onNext(dataWrapper)
            }
        }

        override fun dispose() {
            mReadThread.interrupt()
            mHeartBeatRef?.dispose()
            mSocket.close()
            observer?.onNext(DataWrapper(SocketState.DISCONNECTED, ByteArray(0)))
        }

        override fun isDisposed(): Boolean {
            return mSocket.isConnected
        }
    }

    inner class ReadThread : Thread() {
        override fun run() {
            super.run()
            try {
                while (!mReadThread.isInterrupted && mSocket.isConnected) {
                    val input = DataInputStream(mSocket.getInputStream())
                    var buffer: ByteArray = ByteArray(input.available())
                    if (buffer.isNotEmpty()) {
                        input.read(buffer)
                        observerWrapper.onNext(buffer)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }
}