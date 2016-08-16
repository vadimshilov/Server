package shilov.vadim;

/**
 * Created by vadim on 16.08.16.
 */
public interface History {

    /**
     * добавление нового соощения в историю
     * @param message текст сообщения
     */
    void newMessage(String message);

    /**
     * получить последние 100 сообщений для передачи только что подключившемуся пользователю
     * @return массив сообщений
     */
    String[] getLastMessages();

}
