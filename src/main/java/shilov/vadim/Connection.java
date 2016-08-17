package shilov.vadim;

/**
 * Created by vadim on 16.08.16.
 */
public abstract class Connection extends Thread {

    private Server server;
    private String clientName;

    public Connection(Server server){
        this.server=server;
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
            if(message!=null)
                server.messageReceived(this,message);
            else
                break;
        } while(isConnected()&&message!=null);
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
