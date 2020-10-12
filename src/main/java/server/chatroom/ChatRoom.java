package server.chatroom;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * This class manages the ChatRooms
 */
public class ChatRoom {

    /**
     * A HashMap that stores all ChatRooms and makes it so that they can be looked up by their 4 digit code
     */
    public static HashMap<String, ChatRoom> rooms = new HashMap<>();

    /**
     * This ArrayList contains the sessions that are connected to this ChatRoom.
     */
    private final ArrayList<Session> sessions = new ArrayList<>();

    /**
     * A 4 digit code for the chatroom
     */
    private final String code;

    /**
     * Create a new ChatRoom and assigns a code for it
     */
    public ChatRoom() {
        // create a code for this ChatRoom
        this.code = generateCode();

        // add this ChatRoom to the main collection
        rooms.put(this.code, this);
    }

    /**
     * @return This ChatRoom's code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sends a message to all the connections in the room
     * @param sender The name of the person who sent the message
     * @param message The message
     */
    public void sendMessage(String sender, String message) throws IOException {
        // send the message to every session connected
        for (Session session : sessions) {
            session.getRemote().sendString(sender + ": " + message);
        }
    }

    /**
     * Adds a session to this ChatRoom
     * @param session The session to connect
     * @param name The client's name. Will be used for the welcome message
     */
    public void connectSession(Session session, String name) throws IOException {
        sessions.add(session); // connect the session
        sendMessage("Server", name + " has joined the room."); // welcome message
    }

    /**
     * Removes a session from this ChatRoom
     * @param session The session to remove
     * @param name The client's name to be used for goodbye message
     */
    public void disconnectSession(Session session, String name) throws IOException {
        sessions.remove(session); // disconnect the client
        sendMessage("Server", name + " has left the room."); // goodbye message

        // check if the room has any clients left. If not, delete the room
        if (sessions.size() == 0) {
            rooms.remove(this.code);
        }
    }

    /**
     * Generates a random 4 digit code and checks it against the list to ensure uniqueness
     * @return The code
     */
    private String generateCode() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // the alphabet, used to create the code
        Random random = new Random(); // the Random Number generator

        // create a 4 digit code
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            codeBuilder.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        // finish up and return
        return codeBuilder.toString();
    }

    /**
     * Sends the instructions on how to use the service to the client
     * @param session The session to send to
     * @throws IOException Thrown if an error sending the message occurs.
     */
    public static void sendInstructions(Session session) throws IOException {
        session.getRemote().sendString("Welcome to ChatRoom with WebSockets Example!\n"
                + "To set your name, type /setname <name>\n"
                + "To join a room, type /join <code>\n"
                + "To leave a room, type /leave\n"
                + "To create a room, type /create\n"
                + "Anything else sent will send a message to your chat room.");
    }
}
