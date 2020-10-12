package server.websocket;

import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;

/**
 * This is the class that will be used to attach the WebSocket to the server.
 * The reason that you cannot add a WebSocket directly to a server is that a new instance
 * of the class you specified is created for each connection. This allows for easy access to
 * variables such as the session variable. Other implementations not used in this example
 * pass the variables instead as function arguments.
 */
@WebServlet(urlPatterns="/messages")
public class MessageWrapper extends WebSocketHandler {
    public void configure(WebSocketServletFactory factory) {
        // this binds the WebSocketListener to this WebSocketHandler class, allowing for a
        // server to indirectly interface with the WebSocketListener
        factory.register(MessageWebSocketHandler.class);
    }
}
