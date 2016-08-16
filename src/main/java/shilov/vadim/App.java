package shilov.vadim;

import shilov.vadim.tcp.TcpServer;

/**
 *
 */
public class App {
    public static void main( String[] args ) {
        Server server=new TcpServer();
        if(server.createConnection())server.waitForConnections();
    }
}
