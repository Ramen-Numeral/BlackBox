package game.envsetup;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SetEnv {

    private static final Map<String, String> envMap = new HashMap<String, String>();

    public static Map<String, String> load(String path) {

        try (BufferedReader buff = new BufferedReader(new FileReader(new File(path)))) {
            String line;
            while((line = buff.readLine()) != null){
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eqIdx = line.indexOf('=');
                if(eqIdx == -1)continue;
                envMap.put(line.substring(0, eqIdx).trim().toUpperCase()
                        , line.substring(eqIdx+1).trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("env file read erro");
        }
        return envMap;
    }

    public static String get(String key) {
        if (key == null) return null;
        String k = key.toUpperCase();
        return envMap.getOrDefault(k, System.getenv(k));
    }

    public static String getOrSet(String key, String defaultVal) {
        if (key == null) return defaultVal;
        String k = key.toUpperCase();
        return envMap.computeIfAbsent(k,
                kk -> System.getenv(kk) != null
                        ? System.getenv(kk) : defaultVal);
    }


}
