package game.stateRoutines.envsetup;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

public class SetEnv {


    private static final HashMap<String, String> envMap = new HashMap<>();

    public static HashMap<String, String> load() {
        String[] files = {".env", ".env.secrets"}; //.env are global vars .env.secrets are api keys

        //map all of the variables to an environment map to be called internally
        for(String file : files)
            try (Stream<String> lines = Files.lines(Paths.get(file))) {
                lines.map(String::trim)                          // trim whitespace
                        .filter(line -> !line.isEmpty() && !line.startsWith("#")) // ignore blank/comment lines
                        .forEach(line -> {
                            int eqIdx = line.indexOf('=');
                            if (eqIdx != -1) {
                                envMap.put(line.substring(0, eqIdx).trim().toUpperCase(),
                                        line.substring(eqIdx + 1).trim());
                            }
                        });
            } catch (NoSuchFileException e){
                System.out.println("ERROR: .env.secrets file necessary to play game. Example available at .env.secrets.example. Continuing play will result in failure.");
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
