// the WebSocket is located at ws://<ip>:<port>/<context path>/<WebServlet annotation above WebSocketHandler>
let webSocket = new WebSocket("ws://localhost:8000/messages/messages");

webSocket.onopen = () => {
    // this is where incoming messages go
    webSocket.onmessage = ev => {
        console.log("a");
        messageList.textContent += "\n" + ev.data + "\n";
    };
}

// the elements
let messageList = document.getElementById("messages");
let commandInput = document.getElementById("command");
let sendButton = document.getElementById("send");

sendButton.addEventListener("click", () => {
    // this is how the messages are sent
    webSocket.send(commandInput.value);
    commandInput.value = "";
});

// this is to make it so pressing the enter key has the same effect as pressing the send button
commandInput.addEventListener("keyup", ev => {
    if (ev.code == "Enter") {
        sendButton.click();
    }
});