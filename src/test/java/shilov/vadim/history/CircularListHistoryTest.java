package shilov.vadim.history;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by vadim on 16.08.16.
 */
public class CircularListHistoryTest extends Assert{

    @Test
    public void notEnoughMessagesTest(){
        History history=new CircularListHistory();
        String[] patternArray=new String[30];
        for(int i=0;i<30;i++){
            history.newMessage(""+i);
            patternArray[i]=""+i;
        }
        assertArrayEquals(patternArray,history.getLastMessages());
    }

    @Test
    public void manyMessagesTest(){
        History history=new CircularListHistory();
        String[] patternArray=new String[100];
        for(int i=0;i<130;i++){
            history.newMessage(""+i);
        }
        for(int i=0;i<100;i++)
            patternArray[i]=""+(i+30);
        assertArrayEquals(patternArray,history.getLastMessages());
    }

}
