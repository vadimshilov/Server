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

    private void receiveName(){
        send("Введите имя");
        boolean nameAccepted=false;
        while(isConnected()&&!nameAccepted){
            clientName = null;
            do{
                clientName=receiveData();
            }while (clientName==null&&isConnected());
            nameAccepted=server.nameReceived(this);
            if(!nameAccepted){
                send("Введенное имя занято. Введите другое");
            }
            else{
                send("Добро пожаловть в чат, "+clientName+"!");
            }
        }
    }

    private void waitForMessage(){
        while(isConnected()) {
            String message = receiveData();
            if(message!=null)
                server.messageReceived(this,message);
        }
    }

    public void run(){
        receiveName();
        waitForMessage();
        server.clientDisconnected(this);
    }

    public String getClientName(){
        return clientName;
    }

}
