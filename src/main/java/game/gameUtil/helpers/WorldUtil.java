package game.gameUtil.helpers;

import game.gameUtil.objs.GameLevel;
import game.gameUtil.objs.WorldMap;
import game.stateRoutines.envsetup.SetEnv;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

// Load/save helper class for the world map that holds all levels
public final class WorldUtil {

    public static boolean writeOutWorldMap() {
        String saveFilePath = SetEnv.get("WORLD_SAVE_PATH");
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
    public static boolean loadWorldMap() {
        String saveFilePath = SetEnv.get("WORLD_SAVE_PATH");

        if (saveFilePath == null || saveFilePath.isEmpty()) {
            System.err.println("[WORLD-LOADER] ERROR: Save file path is null or empty.");
            return false;
        }

        File saveFile = new File(saveFilePath);
        if (!saveFile.exists()) {
            System.err.println("[WORLD-LOADER] ERROR: Save file does not exist: " + saveFilePath);
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
            Object objWorld = ois.readObject();
            Object objEmbeddings = ois.readObject();

            if (objWorld instanceof HashMap && objEmbeddings instanceof HashMap) {
                HashMap<Object, Object> loadedWorld = (HashMap<Object, Object>) objWorld;
                HashMap<Object, Object> loadedEmbeddings = (HashMap<Object, Object>) objEmbeddings;

                // Clear and reload WorldMap
                for (Map.Entry<Object, Object> entry : loadedWorld.entrySet()) {
                    Object k = entry.getKey();
                    Object v = entry.getValue();
                    if (k instanceof String && v instanceof GameLevel) {
                        WorldMap.getWorldMapModifiable().put((String) k, (GameLevel) v);
                    }
                }

                // Clear and reload Embeddings
                for (Map.Entry<Object, Object> entry : loadedEmbeddings.entrySet()) {
                    Object k = entry.getKey();
                    Object v = entry.getValue();
                    if (k instanceof String && v instanceof double[]) {
                        WorldMap.getCommandEmbeddingsModifiable().put((String) k, (double[]) v);
                    }
                }

                System.out.println("[WORLD-LOADER] WorldMap successfully loaded from: " + saveFilePath);
                return true;

            } else {
                System.err.println("[WORLD-LOADER] ERROR: File content is invalid.");
                return false;
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[WORLD-LOADER] ERROR: Failed to read WorldMap from file: " + saveFilePath);
            e.printStackTrace();
            return false;
        }
    }
}
