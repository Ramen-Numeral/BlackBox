package game.gameObjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class GameState implements Serializable {

    private static final long serialVersionUID = 1L;
    private Map<String, Boolean> playedStatus = new HashMap<>();
    private String currentLevelCommand;

    public GameState(Map<String, GameLevel> initialWorldMap, String currentLevel) {
        this.currentLevelCommand = currentLevel;
        this.playedStatus = new HashMap<>();
    }

    public GameState() {
        this.currentLevelCommand = "start";
        this.playedStatus = new HashMap<>();
    }

    // -----------------------------------------------
    // --- SAVE/LOAD ROUTINES ---
    // -----------------------------------------------

    /**
     * Creates a minimal, low-overhead save state map by querying the current
     * 'played' status of the full world map.
     */
    public static GameState saveGame(Map<String, GameLevel> currentWorldMap, String currentCommand) {
        GameState saveFile = new GameState(currentWorldMap, currentCommand);

        // Using streams to filter played levels and populate the map
        saveFile.playedStatus = currentWorldMap.values().stream()
                .filter(GameLevel::isPlayed)
                .collect(Collectors.toMap(GameLevel::getCommand, level -> true));

        return saveFile;
    }

    public static void loadGame(Map<String, GameLevel> currentWorldMap, GameState savedGame) {
        // Reset all levels first using streams
        currentWorldMap.values().forEach(level -> level.setPlayed(false));

        // Apply saved played status
        savedGame.playedStatus.forEach((command, wasPlayed) -> {
            GameLevel levelObject = currentWorldMap.get(command);
            if (levelObject != null && wasPlayed != null) {
                levelObject.setPlayed(wasPlayed);
            }
        });
    }

    private static void resetWorld(Map<String, GameLevel> currentWorldMap) {
        currentWorldMap.values().forEach(level -> level.setPlayed(false));
    }

    // -----------------------------------------------
    // --- GETTERS/SETTERS ---
    // -----------------------------------------------
    public String getCurrentLevelCommand() {
        return currentLevelCommand;
    }

    public void setCurrentLevelCommand(String currentLevelCommand) {
        this.currentLevelCommand = currentLevelCommand;
    }
}
