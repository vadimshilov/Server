package shilov.vadim;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by vadim on 16.08.16.
 */
public class ServerTest extends Assert {

    Server server;
    Connection conn1;
    Connection conn2;

    @Before
    public void createServer(){
        server=new Server() {
            @Override
            public boolean createConnection() {
                return false;
            }

            @Override
            protected void waitForSingleConnection() {

            }
        };

        conn1=mock(Connection.class);
        when(conn1.getClientName()).thenReturn("Name1");

        conn2=mock(Connection.class);
        when(conn2.getClientName()).thenReturn("Name2");
    }

    @Test
    public void userNameTest(){

        Connection conn3=mock(Connection.class);
        when(conn3.getClientName()).thenReturn("Name1");

        assertTrue(server.nameReceived(conn1));
        assertTrue(server.nameReceived(conn2));
        assertFalse(server.nameReceived(conn3));

        server.clientDisconnected(conn1);

        assertTrue(server.nameReceived(conn3));
    }

    @Test
    public void removeUserTest(){
        server.nameReceived(conn1);
        assertFalse(server.nameReceived(conn1));//больше нельзя подключиться клиенту с таким именем
        server.clientDisconnected(conn1);
        assertTrue(server.nameReceived(conn1));//имя свнова свободно
    }

    @Test
    public void changeNameTest(){
        server.nameReceived(conn1);
        server.nameReceived(conn2);

        assertFalse(server.changeName(conn2,"Name1"));
        assertFalse(server.changeName(conn2,"Name2"));
        assertTrue(server.changeName(conn2,"Name3"));
        assertFalse(server.changeName(conn2,"Name3"));//теперь Name3 должно быть занято
    }

    @Test
    public void getUserListTest(){
        server.nameReceived(conn1);
        server.nameReceived(conn2);

        String userList=server.getUserList();
        assertTrue(userList.equals("Name1 Name2")||userList.equals("Name2 Name1"));
    }

    @Test
    public void getConnectionCount(){
        assertEquals(server.getConnectionCount(),0);
        server.nameReceived(conn1);
        server.nameReceived(conn2);
        assertEquals(server.getConnectionCount(),2);
    }


}
