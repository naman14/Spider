# Spider

Spider is an Android library to monitor and modify network requests.

# Setup

Include the library using Jitpack
```java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
 ```
    
    
```java
debugImplementation 'com.github.naman14.Spider:spider:1.2'

// No-op version for release builds
releaseImplementation 'com.github.naman14.Spider:spider-noop:1.2'
```

Attach `SpiderInterceptor` to OkHttp client

```kotlin
if (DEBUG) {
    okhttpClient.addInterceptor(SpiderInterceptor.getInstance(context))
}
```

You will be able to monitor the requests at `localhost:6060`

<img src="https://raw.githubusercontent.com/naman14/Spider/master/screenshots/screenshot1.png">
<img src="https://raw.githubusercontent.com/naman14/Spider/master/screenshots/screenshot2.png">
<img src="https://raw.githubusercontent.com/naman14/Spider/master/screenshots/screenshot3.png">

## License

The MIT License

Copyright (c) 2018 Naman Dwivedi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
