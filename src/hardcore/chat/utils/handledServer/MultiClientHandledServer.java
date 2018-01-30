package hardcore.chat.utils.handledServer;

import hardcore.chat.utils.handledServer.handled_interfaces.InterruptableByConnection;
import hardcore.chat.utils.handledServer.threaded_classes.ConnectionsListProcessor;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Кирилл on 25.01.2018.
 */
public class MultiClientHandledServer extends Thread implements Closeable {
    private ConnectionsListProcessor connectionsListProcessor;
    private ServerSocket serverSocket;

    public MultiClientHandledServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        connectionsListProcessor = new ConnectionsListProcessor(serverSocket);
        start();
    }

    public MultiClientHandledServer(int port, InterruptableByConnection connectionHandler) throws IOException {
        this(port);
        connectionsListProcessor.attachConnectionHandler(connectionHandler);
    }

    public ConnectionsListProcessor getConnectionsListProcessor() {
        return connectionsListProcessor;
    }

    public void close() throws IOException {
        connectionsListProcessor.close();
        serverSocket.close();
    }
}
