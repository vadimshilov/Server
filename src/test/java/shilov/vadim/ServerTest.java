package shilov.vadim;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by vadim on 16.08.16.
 */
public class ServerTest extends Assert {

    @Test
    public void userNameTest(){
        Server server=new Server() {
            @Override
            public boolean createConnection() {
                return false;
            }

            @Override
            protected void waitForSingleConnection() {

            }
        };

        Connection conn1=mock(Connection.class);
        when(conn1.getClientName()).thenReturn("Name");

        Connection conn2=mock(Connection.class);
        when(conn2.getClientName()).thenReturn("Name1");

        Connection conn3=mock(Connection.class);
        when(conn3.getClientName()).thenReturn("Name");

        assertTrue(server.nameReceived(conn1));
        assertTrue(server.nameReceived(conn2));
        assertFalse(server.nameReceived(conn3));

        server.clientDisconnected(conn1);

        assertTrue(server.nameReceived(conn3));
    }

    @Test
    public void removeUserTest(){
        Server server=new Server() {
            @Override
            public boolean createConnection() {
                return false;
            }

            @Override
            protected void waitForSingleConnection() {

            }
        };

        Connection conn=mock(Connection.class);
        when(conn.getClientName()).thenReturn("Name");

        server.clientDisconnected(conn);

        assertTrue(server.nameReceived(conn));
    }


}
