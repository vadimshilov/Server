package shilov.vadim;

import shilov.vadim.history.CircularListHistory;
import shilov.vadim.history.History;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by vadim on 16.08.16.
 */
public abstract class Server {

    private ConcurrentHashMap<Connection,String> connectionSet;
    private Set<String> clientNameSet;
    private History history;

    public Server(){
        clientNameSet=new HashSet<>();
        connectionSet=new ConcurrentHashMap<>();
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
    public synchronized boolean nameReceived(Connection source){
        String name=source.getClientName();
        if(clientNameSet.contains(name))return false;
        broadcastSend(name+ "зашел в чат");
        clientNameSet.add(name);
        connectionSet.put(source,name);
        sendHistoryToClient(source);
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
//        System.out.println(message);
        history.newMessage(message);
        long start=System.currentTimeMillis();
        broadcastSend(message);
        long end=System.currentTimeMillis();
        System.err.println(end-start);
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

    class BroadcastSender extends Thread{

        private String message;

        public BroadcastSender(String message){
            this.message=message;
        }

        public void run(){
            for(Connection connection:connectionSet.keySet()){
                connection.send(message);
            }
        }

    }

    private void broadcastSend(String message){
//        for(Connection connection:connectionSet){
//            connection.send(message);
//        }
        new BroadcastSender(message).start();
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
