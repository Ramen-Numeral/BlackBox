package game.gameUtil.helpers;

import game.gameUtil.objs.GameLevel;
import game.gameUtil.objs.GameState;
import game.gameUtil.objs.WorldMap;
import game.stateRoutines.envsetup.SetEnv;
import java.io.*;
import java.util.Map;

/**
 * Utility class for saving and loading GameState.
 */
public final class StateUtil {

    // Save GameState to disk
    public static boolean writeOutState(GameState state, String saveFilePath) {
        if (state == null) {
            System.err.println("[SAVER] ERROR: GameState is null, nothing to save.");
            return false;
        }
        if (saveFilePath == null || saveFilePath.isEmpty()) {
            System.err.println("[SAVER] ERROR: Save file path is null or empty.");
            return false;
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFilePath))) {
            oos.writeObject(state);
            System.out.println("[SAVER] Game state successfully saved to: " + saveFilePath);
            return true;
        } catch (Exception e) {
            System.err.println("[SAVER] ERROR: Failed to write GameState to file: " + saveFilePath);
            e.printStackTrace();
            return false;
        }
    }

    // Load GameState from disk
    public static GameState streamToState(String saveFilePath) {
        if (saveFilePath == null || saveFilePath.isEmpty()) {
            System.err.println("[LOADER] ERROR: Save file path is null or empty.");
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFilePath))) {
            Object obj = ois.readObject();
            if (obj instanceof GameState state) {
                return state;
            } else {
                System.err.println("[LOADER] ERROR: File does not contain a valid GameState.");
            }
        } catch (Exception e) {
            System.err.println("[LOADER] ERROR: Failed to read save file: " + saveFilePath);
            e.printStackTrace();
        }

        return null;
    }

    // Load game and update world map, returning current level command
    public static String loadGame(String saveFilePath) {
        GameState savedGame = streamToState(saveFilePath);
        if (savedGame == null) {
            System.err.println("[LOADER] Could not load save file, using default start state.");
            savedGame = new GameState();
        }

        // Reset all levels
        WorldMap.getWorldMap().values().forEach(level -> level.setPlayed(false));

        // Apply saved played status
        if (savedGame.playedStatus != null) {
            savedGame.playedStatus.forEach((command, wasPlayed) -> {
                GameLevel level = WorldMap.getWorldMap().get(command);
                if (level != null && wasPlayed != null) {
                    level.setPlayed(wasPlayed);
                }
            });
        }

        System.out.println("[LOADER] Game state successfully loaded from: " + saveFilePath);
        return savedGame.currentLevelCommand;
    }

    // Convenience method: save game using default path
    public static GameState saveGame(Map<String, GameLevel> currentWorldMap, String currentCommand) {
        GameState state = new GameState(WorldMap.getWorldMap(), currentCommand);
        writeOutState(state, SetEnv.get("GAME_STATE_PATH"));
        return state;
    }
}
