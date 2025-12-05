package game.gameUtil.objs;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serializable class representing the state of the game.
 */
public final class GameState implements Serializable {

    private static final long serialVersionUID = 1L;
    public Map<String, Boolean> playedStatus;
    public String currentLevelCommand;

    public GameState(Map<String, GameLevel> world, String currentLevel) {
        this.currentLevelCommand = currentLevel;
        this.playedStatus = world.values().stream()
                .filter(GameLevel::isPlayed)
                .collect(Collectors.toMap(GameLevel::getCommand, level -> true));
    }

    public GameState() {
        this.currentLevelCommand = "start";
        this.playedStatus = new HashMap<>();
    }
}
