var ws;

function connect() {
    if ("WebSocket" in window) {
        var host = location.hostname
        var port = parseInt(location.port)
        ws = new WebSocket("ws://" + host + ":" + (port + 1));

        ws.onopen = function () {
            console.log("Connection open");
        };

        ws.onmessage = function (e) {
           var message = e.data
           console.log(message)
        };

        ws.onclose = function () {
            console.log("Connection closed");
        };
    }

    else {
        alert("WebSocket NOT supported by your Browser!");
    }
}
