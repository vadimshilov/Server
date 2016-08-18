package shilov.vadim.history;

import java.util.Arrays;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Класс для хранения истории сообщений в виде циклического списка
 * Created by vadim on 16.08.16.
 */
public class CircularListHistory implements History{
    private static int LIST_SIZE=100;
    private volatile String[] list;
    private volatile int end;

    ReadWriteLock lock;

    public CircularListHistory(){
        list=new String[LIST_SIZE];
        Arrays.fill(list,null);
        lock=new ReentrantReadWriteLock();
    }

    @Override
    public void newMessage(String message) {
        lock.writeLock().lock();
        list[end]=message;
        end=(end+1)%LIST_SIZE;
        lock.writeLock().unlock();
    }

    @Override
    public String[] getLastMessages() {
        lock.readLock().lock();
        String[] result;
        int j=0;
        if(list[end]==null){
            result=new String[end];
        }
        else{
            result=new String[LIST_SIZE];
            for(int i=end;i<LIST_SIZE;i++)result[j++]=list[i];
        }
        for(int i=0;i<end;i++)result[j++]=list[i];
        lock.readLock().unlock();
        return result;
    }
}
