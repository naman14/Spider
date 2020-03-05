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
            return (call.responseReceivedAt - call.requestSentAt) + " ms"
         },
        getRequestTimestampString: function (call) {
             return formatDate(new Date(call.requestSentAt))
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
                
            })
        },
        resetModifiedCall: function(call) {
            this.modifiedNetworkCall = {}
            makeServerCall("http://"+ host + ":" + port +"?command=resetCall", JSON.stringify(call), function(response) {
                
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

function formatDate(date) {
  var hours = date.getHours();
  var minutes = date.getMinutes();
  var seconds = date.getSeconds();
  var ampm = hours >= 12 ? 'PM' : 'AM';
  hours = hours % 12;
  hours = hours ? hours : 12; // the hour '0' should be '12'
  minutes = minutes < 10 ? '0'+minutes : minutes;
  var strTime = hours + ':' + minutes + ":" + seconds + ' ' + ampm;
  return strTime;
}