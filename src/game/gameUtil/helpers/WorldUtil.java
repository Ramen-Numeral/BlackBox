package game.gameUtil.helpers;

import game.gameUtil.objs.GameLevel;
import game.gameUtil.objs.WorldMap;

import java.io.*;
import java.util.HashMap;

/**
 * Utility class for saving and loading WorldMap.
 */
public final class WorldUtil {

    public static boolean writeOutWorldMap(String saveFilePath) {
        if (saveFilePath == null || saveFilePath.isEmpty()) {
            System.err.println("[WORLD-SAVER] ERROR: Save file path is null or empty.");
            return false;
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFilePath))) {
            oos.writeObject(new HashMap<>(WorldMap.getWorldMap()));
            oos.writeObject(new HashMap<>(WorldMap.getAllCommandEmbeddings()));
            System.out.println("[WORLD-SAVER] WorldMap successfully saved to: " + saveFilePath);
            return true;
        } catch (Exception e) {
            System.err.println("[WORLD-SAVER] ERROR: Failed to write WorldMap to file: " + saveFilePath);
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadWorldMap(String saveFilePath) {
        if (saveFilePath == null || saveFilePath.isEmpty()) {
            System.err.println("[WORLD-LOADER] ERROR: Save file path is null or empty.");
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFilePath))) {
            Object objWorld = ois.readObject();
            Object objEmbeddings = ois.readObject();

            if (objWorld instanceof HashMap<?, ?> loadedWorld && objEmbeddings instanceof HashMap<?, ?> loadedEmbeddings) {
                // Clear and reload
                loadedWorld.forEach((k, v) -> {
                    if (k instanceof String && v instanceof GameLevel) {
                        WorldMap.getWorldMap().put((String) k, (GameLevel) v);
                    }
                });
                loadedEmbeddings.forEach((k, v) -> {
                    if (k instanceof String && v instanceof double[]) {
                        WorldMap.getAllCommandEmbeddings().put((String) k, (double[]) v);
                    }
                });
                System.out.println("[WORLD-LOADER] WorldMap successfully loaded from: " + saveFilePath);
                return true;
            } else {
                System.err.println("[WORLD-LOADER] ERROR: File content is invalid.");
            }

        } catch (Exception e) {
            System.err.println("[WORLD-LOADER] ERROR: Failed to read WorldMap from file: " + saveFilePath);
            e.printStackTrace();
        }
        return false;
    }
}
