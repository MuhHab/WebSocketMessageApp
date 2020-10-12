package server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import server.websocket.MessageWrapper;

import java.net.InetSocketAddress;

/**
 * This is the main class
 */
public class ChatServer {

    public static void main(String[] args) throws Exception {
        // create and start the server
        new ChatServer().startServer();
    }

    /**
     * This function creates and starts a ChatServer
      * @throws Exception Thrown if the server has trouble starting
     */
    public void startServer() throws Exception {
        // create the server at localhost:8000
        Server server = new Server(InetSocketAddress.createUnresolved("localhost", 8000));

        // create a ResourceHandler for the HTML
        ResourceHandler fileHandler = new ResourceHandler();
        // this will list files for directories that do not have an index.html
        fileHandler.setDirectoriesListed(true);
        // this makes the default file for when no file is specified "index.html"
        fileHandler.setWelcomeFiles(new String[] {"index.html"});
        // this is the folder where the HTML and JavaScript code is contained
        fileHandler.setResourceBase("website");

        // here we define the WebSocket class that will handle incoming messages
        MessageWrapper messageHandler = new MessageWrapper();

        // here we define the ContextHandler that will delegate the HTML and the WebSocket traffic
        // this ContextHandler will handle the HTML code for when the requested url starts with "/"
        ContextHandler fileHandlerContext = new ContextHandler("/");
        fileHandlerContext.setHandler(fileHandler); // bind the fileHandler to this ContextHandler
        // this ContextHandler will handle the WebSocket connections for urls that start with "/messages"
        ContextHandler messageHandlerContext = new ContextHandler("/messages");
        messageHandlerContext.setHandler(messageHandler); // bind the messageHandler to this Context

        // now we will bind the ContextHandler to a single collection, then bind that to the server
        ContextHandlerCollection handlerCollection = new ContextHandlerCollection();
        handlerCollection.setHandlers(new Handler[] {fileHandlerContext, messageHandlerContext});
        server.setHandler(handlerCollection); // attach to the server

        // now we will start the server
        server.start();
        server.join();
    }

}
