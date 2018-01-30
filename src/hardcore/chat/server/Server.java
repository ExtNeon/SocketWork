package hardcore.chat.server;

import hardcore.chat.utils.ChatSocketUtils;
import hardcore.chat.utils.handledServer.MultiClientHandledServer;
import hardcore.chat.utils.handledServer.handled_interfaces.InterruptableByConnection;
import hardcore.chat.utils.handledServer.threaded_classes.ThreadedHandledConnection;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Кирилл on 25.01.2018.
 */
public class Server implements InterruptableByConnection {

    private final static int MAX_CLIENTS = 4;
    private final static char DATA_END_CHAR = '#';

    private ArrayList<UserProcessor> userProcessors = new ArrayList<>(MAX_CLIENTS);
    private MultiClientHandledServer chatServer;

    Server(int port) throws IOException {
        chatServer = new MultiClientHandledServer(port, this);
        System.out.println("Сервер чата запущен на порту " + port);
    }

    public static void main(String[] args) throws IOException {
        new Server(11255);
    }

    @Override
    public void onNewConnection(ThreadedHandledConnection connection) {
        if (userProcessors.size() >= MAX_CLIENTS) {
            try {
                ChatSocketUtils.justSendTextToSocket(connection.getSocket(), "RT:Server|Disconnected: server is full");
                System.out.println("Попытка подключения клиента #" + (userProcessors.size() + 1) +
                        "(IP: " + connection.getSocket().getInetAddress() +
                        "). Соединение разорвано: достигнуто максимальное количество клиентов");
                connection.close();
            } catch (IOException e) {
                System.err.println("ОШИБКА #12: I/O ERROR.");
            }
        } else {
            userProcessors.add(new UserProcessor(connection, chatServer));
            connection.attachDataHandler(userProcessors.get(userProcessors.size() - 1), DATA_END_CHAR);
            System.out.println("Подключён новый клиент #" + userProcessors.size() +
                    ". IP: " + connection.getSocket().getInetAddress());
        }
    }

    @Override
    public void onDisconnected(ThreadedHandledConnection connection) {
        int clientIndex = -1;
        for (int i = 0; i < userProcessors.size(); i++) {
            if (userProcessors.get(i).getConnection() == connection) {
                clientIndex = i;
                for (ThreadedHandledConnection currentClient : chatServer.getConnectionsListProcessor().getClients()) {
                    try {
                        if (currentClient != connection)
                            ChatSocketUtils.justSendTextToSocket(currentClient.getSocket(), "RT:INFO|" + userProcessors.get(i).getUsername() + " disconnected from server#"); //Ретранслируем. В том числе и отправителю.
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
        System.out.println("Клиент " + (clientIndex > -1 ? "#" + (clientIndex + 1) : "noID") + " (IP: "
                + connection.getSocket().getInetAddress() + ") отключён.");
        if (clientIndex > -1) {
            userProcessors.remove(clientIndex);
        }
    }
}
