package hardcore.chat.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Кирилл on 25.01.2018.
 */
public class ChatSocketUtils {

    public static void justSendTextToSocket(Socket socket, String text) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        for (int i = 0; i < text.length(); i++) {
            outputStream.write((int) text.charAt(i));
        }
    }
}
