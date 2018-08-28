var ws;

$( document ).ready(function() {
    if ("WebSocket" in window) {
        var host = location.hostname
        var port = parseInt(location.port)
        ws = new WebSocket("ws://" + host + ":" + (port + 1));

        ws.onopen = function () {
            console.log("Connection open");
            makeServerCall("http://"+ host + ":" + port +"?command=getDeviceInfo", null, function(response) {
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
      modifiedNetworkCall: {},
      currentNetworkCallExpanded: false,
      deviceName: "",
      packageName: ""
    },
    methods: {
        getResponseTimeString: function (call) {
            if (call.responseReceivedAt == 0) {
                return "Pending"
            }
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
        },
        createModifiedCall: function(call) {
            this.modifiedNetworkCall = JSON.parse(JSON.stringify(call));
        },
        saveModifiedCall: function(call) {
            this.modifiedNetworkCall = call
            makeServerCall("http://"+ host + ":" + port +"?command=updateCall", JSON.stringify(this.modifiedNetworkCall), function(response) {
                console.log("call modified")
            })
        }
    }
  })    
})

function makeServerCall(url, body, callback) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() { 
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
            callback(xmlHttp.responseText);
    }
    xmlHttp.open("POST", url, true);
    xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xmlHttp.send(body);
}