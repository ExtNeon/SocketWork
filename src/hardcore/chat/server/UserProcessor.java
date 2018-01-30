package hardcore.chat.server;

import hardcore.chat.utils.ChatSocketUtils;
import hardcore.chat.utils.handledServer.MultiClientHandledServer;
import hardcore.chat.utils.handledServer.handled_interfaces.InterruptableByData;
import hardcore.chat.utils.handledServer.threaded_classes.ThreadedHandledConnection;

import java.io.IOException;

/**
 * Created by Кирилл on 25.01.2018.
 */
public class UserProcessor implements InterruptableByData {
    private ThreadedHandledConnection connection;
    private String username = "unregistered";
    private boolean registered = false;
    private MultiClientHandledServer service_server;

    public UserProcessor(ThreadedHandledConnection connection, MultiClientHandledServer server) {
        this.connection = connection;
        this.service_server = server;
    }

    public ThreadedHandledConnection getConnection() {
        return connection;
    }

    public String getUsername() {
        return username;
    }

    public boolean isRegistered() {
        return registered;
    }

    @Override
    public void onReceiveDataFromSocket(String data, ThreadedHandledConnection connection) {
        //Для начала
        StringBuilder stringBuilder = new StringBuilder(data);
        //Формат команды: ID:PARAM1|PARAM2#
        //Всё очень просто
        try {
            if (stringBuilder.indexOf(":", 0) > 0 && stringBuilder.indexOf("|", 0) > 0) {
                String commandId = stringBuilder.substring(0, stringBuilder.indexOf(":", 0));
                String[] params = {stringBuilder.substring(stringBuilder.indexOf(":", 0) + 1,
                        stringBuilder.indexOf("|", 0)),
                        stringBuilder.substring(stringBuilder.indexOf("|", 0) + 1, stringBuilder.length() - 1)};
                switch (commandId) {
                    case "REG":
                        username = params[0]; //Пока что без проверки
                        registered = true;
                        ChatSocketUtils.justSendTextToSocket(connection.getSocket(), "SVC:6|1#");
                        for (ThreadedHandledConnection currentClient : service_server.getConnectionsListProcessor().getClients()) {
                            ChatSocketUtils.justSendTextToSocket(currentClient.getSocket(), "RT:INFO|" + username + " logged in#"); //Ретранслируем. В том числе и отправителю.
                        }
                        break;
                    case "SM":
                        if (registered) {
                            System.out.println(username + " >> " + params[1]);
                            for (ThreadedHandledConnection currentClient : service_server.getConnectionsListProcessor().getClients()) {
                                ChatSocketUtils.justSendTextToSocket(currentClient.getSocket(), data); //Ретранслируем. В том числе и отправителю.
                            }
                            ChatSocketUtils.justSendTextToSocket(connection.getSocket(), "SVC:2|1#");
                        } else {
                            ChatSocketUtils.justSendTextToSocket(connection.getSocket(), "SVC:2|-1#");
                        }
                        break;
                    case "DIS":
                        System.out.println(username + " want to leave from hardcore.chat");
                        ChatSocketUtils.justSendTextToSocket(connection.getSocket(), "RT:Server|Good bye!#");
                        connection.interrupt();
                        break;
                }
            } else {
                ChatSocketUtils.justSendTextToSocket(connection.getSocket(), "SVC:12|0#"); //12 - код сервисного ответа, 0 - аргумент.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
