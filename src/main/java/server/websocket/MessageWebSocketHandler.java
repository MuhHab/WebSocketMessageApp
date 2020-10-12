package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import server.chatroom.ChatRoom;

import java.io.IOException;

/**
 * This class is the WebSocket itself that the client will connect to.
 */
public class MessageWebSocketHandler extends WebSocketAdapter {
    // no binary messages will need to be received
    public void onWebSocketBinary(byte[] payload, int offset, int len) {}

    // the client's name
    private String name = "Anonymous";

    // the ChatRoom the client is connected to
    private ChatRoom room;

    // the client's session
    private Session session;

    // this is where incoming messages will be handled
    public void onWebSocketText(String message) {
        try {
            // Here, we will simply check for what a message starts with to differentiate
            // In an actual application, you may wish to use something like JSON
            // or even have multiple WebSocketHandlers
            if (message.startsWith("/join ")) {
                // When the join request is sent, the client is attempting to join a Chat Room.
                // We will extract the code, check against the list of chat rooms, and send a response
                // If a room is found, we will connect the session, and inform the room
                String code = message.replace("/join ", "");
                if (ChatRoom.rooms.containsKey(code)) {
                    // connect this session to the room
                    this.room = ChatRoom.rooms.get(code);
                    this.room.connectSession(this.session, this.name);
                } else {
                    // inform the client that the ChatRoom does not exist
                    this.session.getRemote().sendString("Room not found");
                }
            } else if (message.startsWith("/create")) {
                // When this is sent, the client is trying to create a ChatRoom
                ChatRoom room = new ChatRoom(); // create a ChatRoom
                this.room = room; // attach the room to this session
                room.connectSession(this.session, this.name); // add the client to the room
                // Show the ChatRoom code
                room.sendMessage("Server", "Chat Room created. Code: " + room.getCode());
            } else if (message.startsWith("/set-name ")) {
                // this is where the client is trying to set their name
                // update the name
                String name = message.replace("/set-name ", "");
                if (name.equalsIgnoreCase("Server")) {
                    this.session.getRemote().sendString("Cannot set name to \"Server\"");
                } else {
                    this.name = name;
                    // tell the client the name was updated
                    this.session.getRemote().sendString("Name updated to " + this.name);
                }
            } else if (message.startsWith("/leave")) {
                // the client is trying to leave the room
                // disconnect the client
                this.room.disconnectSession(this.session, this.name);
                // finish disassociating the chat room to this connection
                this.room = null;
                // send the leave success message
                this.session.getRemote().sendString("You have successfully left.");
                // when leaving send the instructions again
                ChatRoom.sendInstructions(this.session);
            } else {
                // if none of the special commands were sent, that means a normal message is being sent
                if (this.room != null) {
                    // send the message
                    this.room.sendMessage(this.name, message);
                } else {
                    // send an error message
                    this.session.getRemote().sendString("You are not in a ChatRoom.");
                    ChatRoom.sendInstructions(this.session);
                }
            }
        } catch (IOException ioe) {
            // on an actual server you'd probably want to have a catch around each command
            ioe.printStackTrace();
        }
    }

    public void onWebSocketClose(int statusCode, String reason) {
        try {
            // the client has disconnected; make them leave the room
            // disconnect the client
            this.room.disconnectSession(this.session, this.name);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void onWebSocketConnect(Session session) {
        // IMPORTANT!!! READ THIS <-----------------------
        // SOP (Single Origin Policy) and CORS (Cross Origin Resource Sharing) *DO NOT* apply to WebSockets.
        // This may allow any website the user visits to potentially steal sensitive information.
        // You may wish to use the following code to limit what domain origins can access the WebSocket.

        /*
        // This code fetches the origin (the domain name that the JavaScript code that made the connection
        // is from), and then checks if it came from your website. If not, it closes the connection.
        if (!session.getUpgradeRequest().getOrigin().equals("http://yourdomain.com")) {
            session.close();
        }
        */

        // this.getSession() command appears to not work, so we must manually store the session
        this.session = session;

        // send instruction to the client on how to use the service
        // send it one second later so that the onMessage event is bound
        try {
            ChatRoom.sendInstructions(session);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onWebSocketError(Throwable cause) {
        // log all errors
        cause.printStackTrace();
    }
}
