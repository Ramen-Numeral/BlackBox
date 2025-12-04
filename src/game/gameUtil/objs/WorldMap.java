package game.gameObjects;

import game.commandUtil.CommandUtil;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

/**
 * Core class for the game world.
 * Holds GameLevel objects and embeddings.
 */
public final class WorldMap {
    private static final HashMap<String, GameLevel> worldMap = new HashMap<>();
    private static final HashMap<String, double[]> commandEmbeddings = new HashMap<>();

    public static void buildLevelMap(String levelTextDir) {
        File dir = new File(levelTextDir);

        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Directory not found or is not a directory: " + levelTextDir);
        }

        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No .txt files found in directory: " + levelTextDir);
        }

        worldMap.clear();
        commandEmbeddings.clear();
        CommandUtil.initGlobalCommands();

        Stream.of(files).forEach(txtFile -> {
            try {
                GameLevel newLev = new GameLevel(txtFile.getAbsolutePath());
                worldMap.put(newLev.getCommand(), newLev);

                double[] embedding = newLev.getEmbedding();
                if (embedding != null) {
                    commandEmbeddings.put(newLev.getCommand(), Arrays.copyOf(embedding, embedding.length));
                }

            } catch (IOException | RuntimeException e) {
                System.err.println("Error processing file " + txtFile.getName() + ": " + e.getMessage());
            }
        });

        if (worldMap.isEmpty()) {
            throw new RuntimeException("No GameLevels were successfully created. Check error logs for details.");
        }
    }

    // --- Accessors ---
    public static Map<String, GameLevel> getWorldMap() {
        if (worldMap.isEmpty()) System.err.println("Warning: worldMap is empty.");
        return worldMap;
    }

    public static GameLevel getLevel(String command) { return worldMap.get(command); }
    public static boolean contains(String com){ return worldMap.containsKey(com); }
    public static Set<String> getLevelCommands() { return Collections.unmodifiableSet(worldMap.keySet()); }
    public static Collection<GameLevel> getAllLevels() { return Collections.unmodifiableCollection(worldMap.values()); }
    public boolean isEmpty() { return worldMap.isEmpty(); }

    public static Set<String> getEmbeddedCommands() { return Collections.unmodifiableSet(commandEmbeddings.keySet()); }
    public static double[] getCommandEmbedding(String command) {
        double[] embedding = commandEmbeddings.get(command);
        return embedding != null ? Arrays.copyOf(embedding, embedding.length) : null;
    }
    public static Map<String, double[]> getAllCommandEmbeddings() { return Collections.unmodifiableMap(commandEmbeddings); }
}
