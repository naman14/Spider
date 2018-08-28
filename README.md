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

<img src="https://raw.githubusercontent.com/naman14/Spider/master/screenshots/screenshot1.png">
<img src="https://raw.githubusercontent.com/naman14/Spider/master/screenshots/screenshot2.png">
<img src="https://raw.githubusercontent.com/naman14/Spider/master/screenshots/screenshot3.png">

## License

>(c) 2018 Naman Dwivedi 

>This is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. 

>This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 

>You should have received a copy of the GNU General Public License along with this app. If not, see <https://www.gnu.org/licenses/>.
