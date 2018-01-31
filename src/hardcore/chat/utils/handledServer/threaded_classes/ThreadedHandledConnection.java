package hardcore.chat.utils.handledServer.threaded_classes;

import hardcore.chat.utils.handledServer.handled_interfaces.InterruptableByConnection;
import hardcore.chat.utils.handledServer.handled_interfaces.InterruptableByData;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Данный класс представляет собой одно сокетное соединение, работа с которым происходит в отдельном потоке.
 * Отличается от стандартной реализации не только многопоточностью, но и возможностью подключения обработчиков.
 * <ol>
 *     <li>Обработчик событий подключения и отключения сокета</li>
 *     <li>Обработчик получения данных по определённому символу</li>
 * </ol>
 * Обработчик подключения и отключения сокета используется в клиентской реализации. Он указывается при создании объекта.
 * Обработчик получения данных используется как в клиентской, так и в серверной реализации. Его суть в том,
 * что при получении данных, они накапливаются в буфере до тех пор, пока не будет обнаружен символ прерывания.
 * Как только это произошло, вызывается обработчик получения данных, а в параметры ему передаются полученные данные.
 * Все обработчики вызываются в отдельных потоках.
 * @author Малякин Кирилл, гр. 15ИТ20.
 */
public class ThreadedHandledConnection extends Thread implements Closeable {
    private Socket socket;
    private InterruptableByData dataHandler = null;
    private InterruptableByConnection connectionHandler;
    private char interruptChar = '\0';
    private StringBuilder receiveStrBuf = new StringBuilder();

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
            connectionHandler.onNewConnection(this);
            while (!isInterrupted()) {
                while ((readedInt = reader.read()) != -1) {
                    receiveStrBuf.append((char) readedInt);
                    if (readedInt == interruptChar) {
                        dataHandler.onReceiveDataFromSocket(receiveStrBuf.toString(), this);
                        clearReceiveBuf(); //Очищаем строку.
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

    /**
     * Возвращает содержимое буфера получения данных в виде строки.
     * @param clear Если истина, то буфер очищается.
     * @return Содержимое буфера приёма данных в виде строки.
     */
    public String getReceiveBufContent(boolean clear) {
        String bufContent = receiveStrBuf.toString();
        if (clear) {
            clearReceiveBuf();
        }
        return bufContent;
    }

    private void clearReceiveBuf() {
        receiveStrBuf.delete(0, receiveStrBuf.length());
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
