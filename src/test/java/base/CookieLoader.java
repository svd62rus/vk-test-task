package base;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Загрузчик заготовленных cookie из property-файлов
 *
 * @author Sushkov Denis
 * @version 1.0
 * @since 2023-12-23
 */
public class CookieLoader {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static HashMap<String, String> getCookiesHashMap(String filename) {
        Properties prop = new Properties();
        if (!readPropertyFile(filename, prop)) return null;
        Map map = prop;
        Map<String, String> typeMap = (Map<String, String>) map;
        return new HashMap<>(typeMap);
    }

    private static boolean readPropertyFile(String filename, Properties prop) {
        InputStream input = null;
        try {
            input = CookieLoader.class.getClassLoader().getResourceAsStream(filename);
            if (input == null) {
                return false;
            }
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
