package game.startupRoutine.envsetup;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class SetEnv {

    private static final HashMap<String, String> envMap = new HashMap<>();

    public static HashMap<String, String> load(String path) {
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            lines.map(String::trim)                          // trim whitespace
                    .filter(line -> !line.isEmpty() && !line.startsWith("#")) // ignore blank/comment lines
                    .forEach(line -> {
                        int eqIdx = line.indexOf('=');
                        if (eqIdx != -1) {
                            envMap.put(line.substring(0, eqIdx).trim().toUpperCase(),
                                    line.substring(eqIdx + 1).trim());
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Env file read error");
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
