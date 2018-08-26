var ws;

$( document ).ready(function() {
    if ("WebSocket" in window) {
        var host = location.hostname
        var port = parseInt(location.port)
        ws = new WebSocket("ws://" + host + ":" + (port + 1));

        ws.onopen = function () {
            console.log("Connection open");
            makeServerCall("http://"+ host + ":" + port +"?command=getDeviceInfo ", function(response) {
                var info  = JSON.parse(response)
                app.deviceName = info.deviceName
                app.packageName = info.packageName
            })
        };

        ws.onmessage = function (e) {
            app.networkCallList = JSON.parse(e.data)
            console.log(app.networkCallList)
        };

        ws.onclose = function () {
            console.log("Connection closed");
        };
    }

    else {
        alert("WebSocket NOT supported by your Browser!");
    }


var app = new Vue({
    el: '#app',
    data: {
      networkCallList: [],
      currentNetworkCall: {},
      deviceName: "",
      packageName: ""
    },
    methods: {
        getResponseTimeString: function (call) {
            return Number((call.responseReceivedAt - call.requestSentAt) / 1000000).toFixed(1) + " ms"
          },
        parseJson: function(jsonString) {
            return JSON.parse(jsonString)
        },
        beautifyJson: function(jsonString) {
            if (jsonString == "") {
                jsonString = "Empty body"
            }
            try {
                return JSON.stringify(JSON.parse(jsonString), null, 4);
            } catch(error) {
                return jsonString
            }
        }
    }
  })    
})

function makeServerCall(url, callback) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() { 
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
            callback(xmlHttp.responseText);
    }
    xmlHttp.open("GET", url, true);
    xmlHttp.send(null);
}