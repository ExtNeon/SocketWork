package learning.basic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

            int request;
            while ((request = clientInputStream.read()) != -1) {
                System.out.println("Принято: " + request++);
                System.out.println("Передаём: " + request);
                clientOutputStream.write(request);
            }
            System.out.println("Клиент отключён");
        }
    }
}
