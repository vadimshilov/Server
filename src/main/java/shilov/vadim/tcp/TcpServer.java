package shilov.vadim.tcp;

import shilov.vadim.ConfigReader;
import shilov.vadim.Connection;
import shilov.vadim.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by vadim on 16.08.16.
 */
public class TcpServer extends Server {

    ServerSocket socket;

    @Override
    public boolean createConnection() {
        ConfigReader configReader=ConfigReader.getInstance();
        int port=configReader.getPort();
        try {
            socket = new ServerSocket(port);
            return true;
        }
        catch (IOException e){
            System.out.println("Can't open connection on port "+port);
            return false;
        }
    }

    @Override
    protected void waitForSingleConnection() {
        try {
            Socket newSocket = socket.accept();
            Connection connection = new TcpConnection(this, newSocket);
            connection.start();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
