package hardcore.chat.client;

import hardcore.chat.utils.ChatSocketUtils;
import hardcore.chat.utils.handledServer.handled_interfaces.InterruptableByConnection;
import hardcore.chat.utils.handledServer.handled_interfaces.InterruptableByData;
import hardcore.chat.utils.handledServer.threaded_classes.ThreadedHandledConnection;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Кирилл on 25.01.2018.
 */
public class Client implements InterruptableByData, InterruptableByConnection {
    private String nickname;

    public Client() throws IOException {
        String hostname = getEnteredString("Введите адрес сервера: ");
        int port = getEnteredIntegerNumber("Введите порт сервера: ");
        nickname = getEnteredString("Введите псевдоним: ");
        System.out.println("Подключение...");
        ThreadedHandledConnection connection = new ThreadedHandledConnection(hostname, port, this, this, '#');
        connection.attachDataHandler(this, '#');
        for (; ; ) {
            ChatSocketUtils.justSendTextToSocket(connection.getSocket(), "SM:" + nickname + "|" + getEnteredString("Мессага: ") + "#");
        }
    }

    public static void main(String[] args) throws IOException {
        new Client();
    }

    private static String getEnteredString(String message) {
        System.out.print(message);
        return new Scanner(System.in).nextLine();
    }

    private static int getEnteredIntegerNumber(String message) {
        System.out.print(message);
        return new Scanner(System.in).nextInt();
    }

    @Override
    public void onReceiveDataFromSocket(String data, ThreadedHandledConnection connection) {
        //Для начала
        StringBuilder stringBuilder = new StringBuilder(data);
        //Формат команды: ID:PARAM1|PARAM2#
        //Всё очень просто
        if (stringBuilder.indexOf(":", 0) > 0 && stringBuilder.indexOf("|", 0) > 0) {
            String commandId = stringBuilder.substring(0, stringBuilder.indexOf(":", 0));
            String[] params = {stringBuilder.substring(stringBuilder.indexOf(":", 0) + 1,
                    stringBuilder.indexOf("|", 0)),
                    stringBuilder.substring(stringBuilder.indexOf("|", 0) + 1, stringBuilder.length() - 1)};
            switch (commandId) {
                case "RT":
                    System.err.println(params[0] + " >> " + params[1]);
                    break;
                case "SM":
                    System.out.println("\r" + params[0] + " >> " + params[1]);
                    break;
                case "SVC":
                    switch (params[0]) {
                        case "6":
                            System.out.println("Регистрация " + (params[1].equals("1") ? " успешна" : " провалена"));
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public void onNewConnection(ThreadedHandledConnection connection) {
        System.out.println("Подключено успешно");
        try {
            ChatSocketUtils.justSendTextToSocket(connection.getSocket(), "REG:" + nickname + "|0#");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnected(ThreadedHandledConnection connection) {
        System.out.println("Соединение разорвано.");
    }
}
