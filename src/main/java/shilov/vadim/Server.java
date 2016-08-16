package shilov.vadim;

import java.util.*;

/**
 * Created by vadim on 16.08.16.
 */
public abstract class Server {

    private Set<Connection> connectionSet;
    private Set<String> clientNameSet;

    public Server(){
        clientNameSet=new HashSet<>();
        connectionSet=new HashSet<>();
    }

    /**
     * создание сервера, готового к ожиданию подключений
     * @return true в случае успеха, false в случае неудачи
     */
    public abstract boolean createConnection();

    /**
     * ожидание подключения и создание подключения клиента
     */
    protected abstract void waitForSingleConnection();


    /**
     * запуск цикла ожидания подключений
     */
    public void waitForConnections(){
        while(true){
            waitForSingleConnection();
        }
    }

    /**
     * получение имени клиента
     * @param source подключение с клиентом, имя которого получено
     * @return true если такого имение еще нет, false в противном случае
     */
    public synchronized boolean nameReceived(Connection source){
        String name=source.getClientName();
        if(clientNameSet.contains(name))return false;
        broadcastSend(name+ "зашел в чат");
        clientNameSet.add(name);
        connectionSet.add(source);
        return true;
    }

    /**
     * получение сообщения от клиента
     * @param source клиент, от которого получено сообщение
     * @param message текст полученного сообщения
     */
    public synchronized void messageReceived(Connection source,String message){
        //добавляем к тексту сообщения имя отправителя и дату
        message=source.getClientName()+" ["+new Date(System.currentTimeMillis()).toString()+" ] >"+message;
        System.out.println(message);
        broadcastSend(message);
    }

    private void broadcastSend(String message){
        for(Connection connection:connectionSet){
            connection.send(message);
        }
    }

    /**
     * отключение клиента
     * @param connection отключившийся клиент
     */
    public synchronized void clientDisconnected(Connection connection){
        String clientName=connection.getClientName();
        clientNameSet.remove(clientName);
        connectionSet.remove(connection);
        broadcastSend(clientName+" покинул чат");
    }

}
