package game.gameObjects;

import game.commandProcesses.CommandProcessor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

/**
 * Utility class responsible for scanning the file system, processing level files,
 * precomputing resources (audio, embeddings), and building the foundational
 * level map for the game world.
 */
public final class WorldMap {
    private static final HashMap<String, GameLevel> worldMap = new HashMap<>();
    private static final HashMap<String, double[]> commandEmbeddings = new HashMap<>();

    public static void buildLevelMap(String levelTextDir) {
        File dir = new File(levelTextDir);

        // 1. Directory Validation
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Directory not found or is not a directory: " + levelTextDir);
        }

        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No .txt files found in directory: " + levelTextDir);
        }

        // Clear maps for fresh build
        worldMap.clear();
        commandEmbeddings.clear();

        // Initialize global commands
        CommandProcessor.initGlobalCommands();

        // 2. Process files using Streams
        Stream.of(files).forEach(txtFile -> {
            try {
                String txtPath = txtFile.getAbsolutePath();
                GameLevel newLev = new GameLevel(txtPath);

                // Store in world map
                worldMap.put(newLev.getCommand(), newLev);

                // Store embedding safely
                double[] embedding = newLev.getEmbedding();
                if (embedding != null) {
                    commandEmbeddings.put(newLev.getCommand(), Arrays.copyOf(embedding, embedding.length));
                }

            } catch (IOException | RuntimeException e) {
                System.err.println("Error processing file " + txtFile.getName() + ": " + e.getMessage());
            }
        });

        // 3. Ensure map is not empty
        if (worldMap.isEmpty()) {
            throw new RuntimeException("No GameLevels were successfully created. Check error logs for details.");
        }
    }

    // --- Access Getters for World Map ---

    public static Map<String, GameLevel> getWorldMap() {
        if (worldMap.isEmpty()) {
            System.err.println("Warning: Attempted to access worldMap before initialization. Map is empty.");
        }
        return worldMap;
    }

    public static GameLevel getLevel(String command) {
        return worldMap.get(command);
    }

    public static Set<String> getLevelCommands() {
        return Collections.unmodifiableSet(worldMap.keySet());
    }

    public static Collection<GameLevel> getAllLevels() {
        return Collections.unmodifiableCollection(worldMap.values());
    }

    // --- Access Getters for Embeddings Map ---

    public static Set<String> getEmbeddedCommands() {
        return Collections.unmodifiableSet(commandEmbeddings.keySet());
    }

    public static double[] getCommandEmbedding(String command) {
        double[] embedding = commandEmbeddings.get(command);
        return embedding != null ? Arrays.copyOf(embedding, embedding.length) : null;
    }

    public static Map<String, double[]> getAllCommandEmbeddings() {
        return Collections.unmodifiableMap(commandEmbeddings);
    }
}
