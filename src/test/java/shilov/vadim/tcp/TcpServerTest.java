package shilov.vadim.tcp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import shilov.vadim.ConfigReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by vadim on 16.08.16.
 */
public class TcpServerTest extends Assert{

    TcpServer server;
    Thread serverThread;
    int port;

    @Before
    public void createServer(){
        port= ConfigReader.getInstance().getPort();
        server=new TcpServer();
        serverThread=new Thread(new Runnable() {
            @Override
            public void run() {
                if(server.createConnection())server.waitForConnections();
            }
        });
        serverThread.start();
    }

    @Test(timeout = 1000)
    public void testConnection() throws IOException {
        //создадим 2 подключения и посмотрим, как между ними передаются сообщения
        Socket socket1 = new Socket("127.0.0.1", port);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
        PrintWriter out1 = new PrintWriter(socket1.getOutputStream(), true);
        in1.readLine();
        out1.println("Name");
        in1.readLine();

        Socket socket2 = new Socket("127.0.0.1", port);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
        PrintWriter out2 = new PrintWriter(socket2.getOutputStream(), true);
        in2.readLine();
        out2.println("Name1");
        in2.readLine();

        out2.println("message");

        in1.readLine();
        in2.readLine();

        out1.println("message");

        in1.readLine();
        in2.readLine();
    }

    @After
    public void stopThread(){
        serverThread.interrupt();
    }
}
