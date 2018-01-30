package learning.extended;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Кирилл on 25.01.2018.
 */
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Слушаем на порту 8080...");

        try (Socket connectedClient = serverSocket.accept(); InputStream clientInputStream = connectedClient.getInputStream();
             OutputStream clientOutputStream = connectedClient.getOutputStream()) {
            System.out.println("Подключён клиент, адрес: " + connectedClient.getInetAddress());

            int readedInt;
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientInputStream, "UTF-8"));
            StringBuilder gettedStr = new StringBuilder();
            while ((readedInt = reader.read()) != -1) {
                gettedStr.append((char) readedInt);
                if ((char) readedInt == '\n') {
                    System.out.println("Получено " + gettedStr.toString());
                    gettedStr.delete(0, gettedStr.length() - 1);
                }
            }

            System.out.println("Клиент отключён");
        }
    }
}
