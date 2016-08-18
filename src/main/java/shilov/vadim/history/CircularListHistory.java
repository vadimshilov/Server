package shilov.vadim.history;

import java.util.Arrays;

/**
 * Класс для хранения истории сообщений в виде циклического списка
 * Created by vadim on 16.08.16.
 */
public class CircularListHistory implements History{
    private static int LIST_SIZE=100;
    private String[] list;
    private int end;

    public CircularListHistory(){
        list=new String[LIST_SIZE];
        Arrays.fill(list,null);
    }

    @Override
    public void newMessage(String message) {
        list[end]=message;
        end=(end+1)%LIST_SIZE;
    }

    @Override
    public String[] getLastMessages() {
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
        return result;
    }
}
