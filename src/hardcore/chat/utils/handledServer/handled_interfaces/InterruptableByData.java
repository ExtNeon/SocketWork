package hardcore.chat.utils.handledServer.handled_interfaces;

import hardcore.chat.utils.handledServer.threaded_classes.ThreadedHandledConnection;

/**
 * Интерфейс с методом - обработчиком, вызывающимся, если в потоке данных найден прерывающий символ
 */
public interface InterruptableByData {

    /**
     * Вызывается, в случае, если в принимаемом потоке данных обнаружен ключевой разделительный символ, указанный при подключении обработчика.
     *
     * @param data       Данные до ключевого символа включительно.
     * @param connection Соединение, вызвавшее обработчик.
     */
    void onReceiveDataFromSocket(String data, ThreadedHandledConnection connection);
}
