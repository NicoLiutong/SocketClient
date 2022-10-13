# SocketClient

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/codeestX/RxSocketClient/pulls) [![API](https://img.shields.io/badge/API-20%2B-brightgreen.svg)](https://android-arsenal.com/api?level=20) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)[![](https://jitpack.io/v/codeestX/RxSocketClient.svg)](https://jitpack.io/#codeestX/RxSocketClient)

SocketClient，支持Android，Java和Kotlin的响应式Socket APIs封装，基于RxJava2

RxJava2 Version: 2.1.1

# Usage

Step 1. Add the JitPack repository to your build file

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
   
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.codeestX:SocketClient:v1.0.0'
	}
	
### init
```java
SocketClient mClient = RxSocketClient
        .create(new SocketConfig.Builder()
                .setIp(IP)
                .setPort(PORT)
                .setCharset(Charsets.UTF_8)
                .setThreadStrategy(ThreadStrategy.ASYNC)
                .setTimeout(30 * 1000)
                .build())
        .option(new SocketOption.Builder()
                .setHeartBeat(HEART_BEAT, 60 * 1000)
                .setHead(HEAD)
                .setTail(TAIL)
                .build());

```
| value | default | description |
| :--: | :--: | :--: |
| Ip | required | host address |
| Port | required | port number |
| Charset | UTF_8 | the charset when encode a String to byte[] |
| ThreadStrategy | Async | sending data asynchronously or synchronously|
| Timeout | 0 | the timeout of a connection, millisecond |
| HeartBeat | Optional | value and interval of heartbeat, millisecond |
| Head | Optional | appending bytes at head when sending data, not included heartbeat |
| Tail | Optional | appending bytes at last when sending data, not included heartbeat |

### connect
```java
Disposable ref = mClient.connect()
	... // anything else what you can do with RxJava
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SocketSubscriber() {
                    @Override
                    public void onConnected() {
                        //onConnected
                        Log.e(TAG, "onConnected");
                    }

                    @Override
                    public void onDisconnected() {
                        //onDisconnected
                        Log.e(TAG, "onDisconnected");
                    }

                    @Override
                    public void onResponse(@NotNull byte[] data) {
                        //receive data
                        Log.e(TAG, Arrays.toString(data));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //onError
                        Log.e(TAG, throwable.toString());
                    }
                });
```

### disconnect
```java
mClient.disconnect();
//or
ref.dispose();
```

### sendData
```java
mClient.sendData(bytes);
//or
mClient.sendData(string);
```


