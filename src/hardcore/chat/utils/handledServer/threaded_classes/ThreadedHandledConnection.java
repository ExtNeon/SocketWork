package hardcore.chat.utils.handledServer.threaded_classes;

import hardcore.chat.utils.handledServer.handled_interfaces.InterruptableByConnection;
import hardcore.chat.utils.handledServer.handled_interfaces.InterruptableByData;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Кирилл on 25.01.2018.
 */
public class ThreadedHandledConnection extends Thread implements Closeable {
    private Socket socket;
    private InterruptableByData dataHandler = null;
    private InterruptableByConnection connectionHandler;
    private char interruptChar = '\0';

    public ThreadedHandledConnection(Socket clientSocket, InterruptableByConnection connectionHandler) {
        socket = clientSocket;
        this.connectionHandler = connectionHandler;
        start();
    }

    public ThreadedHandledConnection(String hostname, int port, InterruptableByConnection connectionHandler, InterruptableByData dataHandler, char dataHandlerInterrupptChar) throws IOException {
        this(new Socket(hostname, port), connectionHandler);
        this.attachDataHandler(dataHandler, dataHandlerInterrupptChar);
    }

    public void run() {
        try (InputStream inputStream = socket.getInputStream()) {
            int readedInt;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder gettedStr = new StringBuilder();
            connectionHandler.onNewConnection(this);
            while (!isInterrupted()) {
                while ((readedInt = reader.read()) != -1) {
                    gettedStr.append((char) readedInt);
                    if (readedInt == interruptChar) {
                        dataHandler.onReceiveDataFromSocket(gettedStr.toString(), this);
                        gettedStr.delete(0, gettedStr.length()); //Очищаем строку.
                    }
                }
            }
        } catch (SocketException ignored) { //Пропускаем, так как всё равно при любом сокетном исключении, всё закроется корректно.
            //System.err.println("Well, it is right?\n" + e.toString() + "\nDon\'t care! The program stay working.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connectionHandler.onDisconnected(this);
        }
    }

    public void attachDataHandler(InterruptableByData handler, char handlerDataInterrupter) {
        this.dataHandler = handler;
        interruptChar = handlerDataInterrupter;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void close() {
        interrupt();
    }
}
