package hardcore.chat.utils.handledServer.threaded_classes;

import hardcore.chat.utils.handledServer.handled_interfaces.InterruptableByConnection;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Данный класс в отдельном потоке обрабатывает подключения и отключения клиентов, реализуя многопользовательскую структуру.
 * Список клиентов сохраняется в отдельном массиве, и к нему всегда можно обратиться.
 * Также, можно подключить обработчики событий подключения и отключения клиентов, которые вызываются в отдельном потоке.
 * @author Малякин Кирилл, гр 15ИТ20.
 */
public class ConnectionsListProcessor extends Thread implements InterruptableByConnection, Closeable {
    private volatile ArrayList<ThreadedHandledConnection> clients = new ArrayList<>();
    private ServerSocket serverSocket;
    private InterruptableByConnection connectionHandler = null;

    public ConnectionsListProcessor(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        start();
    }

    public void run() {
        try {
            while (!serverSocket.isClosed() && !isInterrupted()) {
                new ThreadedHandledConnection(serverSocket.accept(), this);
            }
        } catch (SocketException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for (ThreadedHandledConnection currentClient : clients) {
                currentClient.close();
            }
        }
    }

    public ArrayList<ThreadedHandledConnection> getClients() {
        return clients;
    }

    public void attachConnectionHandler(InterruptableByConnection handler) {
        this.connectionHandler = handler;
    }

    @Override
    public void onNewConnection(ThreadedHandledConnection connection) {
        clients.add(connection);
        if (connectionHandler != null) {
            connectionHandler.onNewConnection(connection);
        }
    }

    @Override
    public void onDisconnected(ThreadedHandledConnection connection) {
        if (connectionHandler != null) {
            connectionHandler.onDisconnected(connection);
        }
        clients.remove(clients.indexOf(connection));
    }

    @Override
    public void close() {
        interrupt();
    }
}
