# Spider

Spider is an Android library to monitor and modify network requests.

# Setup

Include the library using Jitpack

```java
implementation 'com.github.naman14:Spider:1.0'
```

Attach `SpiderInterceptor` to OkHttp client

```kotlin
if (DEBUG) {
    okhttpClient.addInterceptor(SpiderInterceptor.getInstance(context))
}
```

You will be able to monitor the requests at `localhost:6060`
