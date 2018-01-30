package hardcore.chat.utils.handledServer.handled_interfaces;

import hardcore.chat.utils.handledServer.threaded_classes.ThreadedHandledConnection;

/**
 * Интерфейс с методами - обработчиками, вызывающимися при новом подключении и отключении клиента.
 */
public interface InterruptableByConnection {
    /**
     * Вызывается тогда, когда происходит подключение клиента.
     *
     * @param connection Соединение, вызвавшее обработчик
     */
    void onNewConnection(ThreadedHandledConnection connection);

    /**
     * Вызывается в случае, если соединение разорвано.
     *
     * @param connection Соединение, вызвавшее обработчик
     */
    void onDisconnected(ThreadedHandledConnection connection);
}
