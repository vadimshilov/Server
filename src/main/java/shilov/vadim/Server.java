package shilov.vadim;

import shilov.vadim.history.CircularListHistory;
import shilov.vadim.history.History;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by vadim on 16.08.16.
 */
public abstract class Server {

    private CopyOnWriteArrayList<Connection> connectionSet;
    private ConcurrentSkipListSet<String> clientNameSet;
    private History history;

    public Server(){
        clientNameSet=new ConcurrentSkipListSet<>();
        
        connectionSet=new CopyOnWriteArrayList<>()                                                                                                                                                                                                                                                                                                                                                                                         ;
        history=new CircularListHistory();
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
    public boolean nameReceived(Connection source){
        String name=source.getClientName();
        if(clientNameSet.contains(name))return false;
        broadcastSend(name+ " зашел в чат");
        clientNameSet.add(name);
        connectionSet.add(source);
        sendHistoryToClient(source);
        return true;
    }


    /**
     * получение сообщения от клиента
     * @param source клиент, от которого получено сообщение
     * @param message текст полученного сообщения
     */
    public void messageReceived(Connection source,String message){
        //добавляем к тексту сообщения имя отправителя и дату
        message=source.getClientName()+" ["+new Date(System.currentTimeMillis()).toString()+" ] >"+message;
        history.newMessage(message);
        broadcastSend(message);
    }

    /**
     * сменить имя клиента
     * @param connection соединение с клиентом
     * @param newName новое имя клиента
     * @return true если удалось сменить имя, false в противном случае
     */
    public boolean changeName(Connection connection,String newName){
        if(clientNameSet.contains(newName))return false;
        clientNameSet.remove(connection.getClientName());
        clientNameSet.add(newName);
        String message=String.format("Пользователь %s сменил имя на %s",connection.getClientName(),newName);
        broadcastSend(message,connection);
        return true;
    }

    /**
     *
     * @return строка, содержащая имена пользователей, разделенные пробелом
     */
    public String getUserList(){
        StringBuilder result=new StringBuilder();
        for(String userName:clientNameSet)result.append(userName).append(" ");
        result.deleteCharAt(result.length()-1);//удаляем последний пробел
        return result.toString();
    }

    /**
     *
     * @return количество подключеных пользователей
     */
    public int getConnectionCount(){
        return connectionSet.size();
    }

    private void sendHistoryToClient(Connection connection){
        String[] messages=history.getLastMessages();
        StringBuilder historyString=new StringBuilder();
        for(String str:messages){
            historyString.append(str);
            historyString.append("\r\n");
        }
        connection.send(historyString.toString());
    }

    private void broadcastSend(String message){
        for(Connection connection:connectionSet)connection.send(message);
    }

    private void broadcastSend(String message, Connection except){//рассылка сообщений всем клиентам, кроме одного
        for(Connection connection:connectionSet)
            if(connection!=except)
                connection.send(message);
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
