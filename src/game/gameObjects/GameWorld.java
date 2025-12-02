package game.gameObjects; // Keeping it in a setup/routine package

//Intended to run once to populate the world and be stored in docker image
//serialize into a .bin file for docker

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class responsible for scanning the file system, processing level files,
 * precomputing resources (audio, embeddings), and building the foundational
 * level map for the game world.
 */
public final class GameWorldBuilder {


    public Map<String, GameLevel> buildLevelMap(String levelTextDir) {
        File dir = new File(levelTextDir);

        // 1. Directory Validation
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Directory not found or is not a directory: " + levelTextDir);
        }

        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No .txt files found in directory: " + levelTextDir);
        }

        // Initialize the map to hold the results
        HashMap<String, GameLevel> levelMap = new HashMap<>();

        // 2. Process Files
        for (File txtFile : files) {
            try {
                String txtPath = txtFile.getAbsolutePath();
                // Create the GameLevel, which handles pre-computation (TTS, Embedding)
                GameLevel newLev = new GameLevel(txtPath);

                // Use the level's command as the key for fast lookups
                levelMap.put(newLev.getCommand(), newLev);
            } catch (IOException | RuntimeException e) {
                // Catch IO errors (file read) or RuntimeExceptions (e.g., parsing/API failure)
                System.err.println("Error processing file " + txtFile.getName() + ": " + e.getMessage());
            }
        }

        // 3. Return the map
        if (levelMap.isEmpty()) {
            throw new RuntimeException("No GameLevels were successfully created. Check error logs for details.");
        }

        return levelMap;
    }
}