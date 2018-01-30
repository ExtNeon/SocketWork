package learning.basic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Кирилл on 25.01.2018.
 */
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 8080);
        try (InputStream inputStream = socket.getInputStream(); OutputStream outputStream = socket.getOutputStream()) {
            int response = 0;
            outputStream.write(response);
            System.out.println("Передали " + response);
            while ((response = inputStream.read()) != -1) {
                System.out.println("Получили " + response);
                if (++response > 10) {
                    break;
                }
                outputStream.write(response);
                System.out.println("Передали " + response);
            }
        }
    }
}
