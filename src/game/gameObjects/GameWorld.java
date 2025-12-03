package game.gameObjects;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Utility class responsible for scanning the file system, processing level files,
 * precomputing resources (audio, embeddings), and building the foundational
 * level map for the game world.
 */
public final class GameWorld {
    private static final HashMap<String, GameLevel> worldMap = new HashMap<>();

    // Static map to hold Command -> Embedding vector for quick access/comparison
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

        // Clear maps to ensure a fresh build
        worldMap.clear();
        commandEmbeddings.clear();

        // 2. Process Files
        for (File txtFile : files) {
            try {
                String txtPath = txtFile.getAbsolutePath();
                // Create the GameLevel, which handles pre-computation (TTS, Embedding)
                GameLevel newLev = new GameLevel(txtPath);

                // Use the level's command as the key for fast lookups
                worldMap.put(newLev.getCommand(), newLev);

                // Store embedding separately for quick access
                // IMPORTANT: Store a copy of the array to prevent modification of the GameLevel's internal array
                double[] embedding = newLev.getEmbedding();
                if (embedding != null) {
                    commandEmbeddings.put(newLev.getCommand(), Arrays.copyOf(embedding, embedding.length));
                }

            } catch (IOException | RuntimeException e) {
                // Catch IO errors (file read) or RuntimeExceptions (e.g., parsing/API failure)
                System.err.println("Error processing file " + txtFile.getName() + ": " + e.getMessage());
            }
        }

        // 3. Return the map
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

    /**
     * Retrieves an unmodifiable collection of all GameLevel objects.
     * @return An unmodifiable Collection of GameLevel objects.
     */
    public static Collection<GameLevel> getAllLevels() {
        return Collections.unmodifiableCollection(worldMap.values());
    }

    // --- Access Getters for Embeddings Map ---

    /**
     * Retrieves an unmodifiable set of all command keys that have associated embeddings.
     * @return An unmodifiable Set of String keys (commands).
     */
    public static Set<String> getEmbeddedCommands() {
        return Collections.unmodifiableSet(commandEmbeddings.keySet());
    }

    /**
     * Retrieves the embedding vector associated with a specific command.
     *
     * @param command The unique access command for the level.
     * @return A copy of the double array (embedding), or null if the command is not found.
     */
    public static double[] getCommandEmbedding(String command) {
        double[] embedding = commandEmbeddings.get(command);
        // Returns a defensive copy to prevent external modification of the stored array
        return embedding != null ? Arrays.copyOf(embedding, embedding.length) : null;
    }

    /**
     * Retrieves an unmodifiable map of all command-embedding pairs.
     * @return An unmodifiable Map where keys are commands (String) and values are embedding vectors (double[]).
     */
    public static Map<String, double[]> getAllCommandEmbeddings() {
        // NOTE: While the map itself is unmodifiable, the double[] arrays inside
        // could technically be modified if exposed. Using this getter assumes
        // the consumer will treat the contents read-only.
        return Collections.unmodifiableMap(commandEmbeddings);
    }
}