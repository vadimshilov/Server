package shilov.vadim.tcp;

import shilov.vadim.Connection;
import shilov.vadim.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by vadim on 16.08.16.
 */
public class TcpConnection extends Connection {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    @Override
    public synchronized void send(String message) {
        out.println(message);
    }

    public TcpConnection(Server server, Socket socket) throws IOException {
        super(server);
        this.socket=socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    protected boolean isConnected() {
        return socket.isConnected();
    }

    @Override
    protected String receiveData() {
        try {
            return in.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
