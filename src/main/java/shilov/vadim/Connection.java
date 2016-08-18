package shilov.vadim;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by vadim on 16.08.16.
 */
public abstract class Connection extends Thread {

    private static final String HELP_COMMAND="\\help";
    private static final String USERS_COMMAND="\\users";
    private static final String CHANGE_NAME_COMMAND="\\change_name";
    private static final String USER_COUNT_COMMAND="\\user_count";

    private Server server;
    private String clientName;
//    private MessageQueue messageQueue;
    private BlockingQueue<String> messageQueue;
    private SendThread sendThread;

    private class SendThread extends Thread{

        private volatile boolean finished=false;

        public void run(){
            while(!messageQueue.isEmpty()){
                send(messageQueue.poll());
            }
            finished=true;
        }

        public synchronized boolean isFinished(){
            return finished;
        }


    }

    public Connection(Server server){
        this.server=server;
        messageQueue=new LinkedBlockingQueue<>();
        sendThread=new SendThread();
        sendThread.start();
    }

    /**
     * отправляет сообщение клиенту
     * @param message сообщение для отправки
     */
    public abstract void send(String message);

    /**
     * проверяет наличие соединения с клиентом
     * @return true при наличие соединения, false в противном случае
     */
    protected abstract boolean isConnected();

    /**
     * ожидает получения данных от клиента
     * @return полученная строка или null если данные получить не удалось
     */
    protected abstract String receiveData();

    //возвращаем false, если не удалось получить ответ от клиента
    private boolean receiveName(){
        send("Введите имя");
        boolean nameAccepted=false;
        while(isConnected()&&!nameAccepted){
            clientName = receiveData();
            if(clientName!=null) {
                nameAccepted = server.nameReceived(this);
                if (!nameAccepted) {
                    send("Введенное имя занято. Введите другое");
                } else {
                    send("Добро пожаловть в чат, " + clientName + "!");
                }
            }
            else
                return false;
        }
        return true;
    }

    private void waitForMessage(){
        String message=null;
        do {
            message = receiveData();
            if(message!=null) {
                execCommand(message);
            }
        } while(isConnected()&&message!=null);
    }

    private void execCommand(String command){//выполнить команду, если она является командой, в противном случае разослать сообщение
        if(command.equals(HELP_COMMAND))
            helpCommand();
        else if(command.equals(USERS_COMMAND))
            usersCommand();
        else if(command.startsWith(CHANGE_NAME_COMMAND))
            changeNameCommand(command);
        else if(command.equals(USER_COUNT_COMMAND))
            userCountCommand();
        else
            server.messageReceived(this,command);
    }



    private void helpCommand(){
        String response=
                HELP_COMMAND + " - справки о доступных командах\r\n" +
                USERS_COMMAND + " - список подключенных пользователей\r\n"+
                CHANGE_NAME_COMMAND + " <имя> - сменить имя\r\n"+
                USER_COUNT_COMMAND+ " - количество подключенных пользователей";
        send(response);
    }

    private void changeNameCommand(String command){
        int beginIndex=command.indexOf(' ')+1;
        String newName=command.substring(beginIndex);
        if(server.changeName(this,newName)) {
            send("Вы успешно сменили имя");
            clientName=newName;
        }
        else
            send("Не удается сменить имя. Введенное имя занять");
    }

    private void userCountCommand(){
        send("Всего подключено пользователей: "+server.getConnectionCount());
    }


    private void usersCommand(){
        send(server.getUserList());
    }

    public void run(){
        if(receiveName()) {
            waitForMessage();
            server.clientDisconnected(this);
        }
    }

    public String getClientName(){
        return clientName;
    }

}
