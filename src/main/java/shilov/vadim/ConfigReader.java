package shilov.vadim;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Класс для чтения настроек из файла
 * Created by vadim on 16.08.16.
 */
public class ConfigReader {

    private static ConfigReader instance=null;
    private static String FILE_NAME="config.properties";

    private static String PORT_PROPERTY="server.port";
    private static int DEFAULT_PORT=1122;

    private Properties properties;

    public static ConfigReader getInstance(){
        if(instance==null) instance=new ConfigReader();
        return instance;
    }

    private ConfigReader(){
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream(FILE_NAME);
            properties.load(fis);
        }
        catch (IOException e){
            properties=null;
            System.out.println("Can't load config. Using default port "+DEFAULT_PORT);
        }
    }

    /**
     * получает номер порта
     * @return номер порта, прочитанный из файлы; если значение не удалось получить из файла, возвращает порт по умолчанию
     */
    public int getPort(){
        if(properties!=null) {
            String portStr=properties.getProperty(PORT_PROPERTY);
            int result=DEFAULT_PORT;
            if(portStr!=null){
                try {
                    result = Integer.parseInt(portStr);
                }catch (NumberFormatException e){
                    //если не удалось распарсить строку, порт останется по умолчанию
                }
            }
            return result;
        }
        else
            return DEFAULT_PORT;
    }

}
