package game.gameObjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public final class GameState implements Serializable {


    private static final long serialVersionUID = 1L;
    private Map<String, Boolean> playedStatus = new HashMap<>();
    private String currentLevelCommand;


    public GameState(Map<String, GameLevel> initialWorldMap, String currentLevel) {
        // Initialize the state based on a new game (nothing played)
        this.currentLevelCommand = currentLevel;
        this.playedStatus = new HashMap<>();
    }
    public GameState() {
        this.currentLevelCommand = "start"; // Default to the first level command or "start"
        this.playedStatus = new HashMap<>();
    }

    // -----------------------------------------------
    // --- SAVE/LOAD ROUTINES (Low Overhead) ---
    // -----------------------------------------------

    /**
     * Creates the minimal, low-overhead save state map by querying the current
     * 'played' status of the full world map.
     *
     * @param currentWorldMap The global, fully initialized map of all levels.
     * @return A serializable Map (Command -> True) containing only levels that have been played.
     */
    public static GameState saveGame(Map<String, GameLevel> currentWorldMap, String currentCommand) {
        GameState saveFile = new GameState(currentWorldMap, currentCommand);
        for (GameLevel level : currentWorldMap.values()) {
            if (level.isPlayed()) {
                saveFile.playedStatus.put(level.getCommand(), true);
            }
        }
        return saveFile;
    }


    public static void loadGame(Map<String, GameLevel> currentWorldMap, GameState savedGame) {
        // Reset the 'played' state of all levels in the world map before applying the saved state
        for (GameLevel level : currentWorldMap.values()) {
            level.setPlayed(false);
        }

        for (Map.Entry<String, Boolean> savedEntry : savedGame.playedStatus.entrySet()) {
            String command = savedEntry.getKey();
            Boolean wasPlayed = savedEntry.getValue();

            GameLevel levelObject = currentWorldMap.get(command);

            if (levelObject != null && wasPlayed != null) {
                levelObject.setPlayed(wasPlayed);
            }
        }
    }

    private static void resetWorld(Map<String, GameLevel> currentWorldMap) {
        for (GameLevel level : currentWorldMap.values()) {
            level.setPlayed(false);
        }
    }

    // -----------------------------------------------
    // --- GETTERS ---
    // -----------------------------------------------
    /**
     * @return The command string for the current starting/resume point.
     */
    public String getCurrentLevelCommand() {
        return currentLevelCommand;
    }


    /**
     * Updates the current resume point. Called when the player moves to a new level.
     * @param currentLevelCommand The command of the newly reached level.
     */
    public void setCurrentLevelCommand(String currentLevelCommand) {
        this.currentLevelCommand = currentLevelCommand;
    }

}