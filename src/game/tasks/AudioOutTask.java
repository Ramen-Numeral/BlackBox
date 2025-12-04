package game.tasks;

import game.gameUtil.objs.GameLevel;
import game.gameUtil.helpers.LevelUtil;

import java.util.concurrent.Callable;

public class AudioOutTask implements Callable<Void> {

    private final GameLevel level;

    public AudioOutTask(GameLevel level) {
        this.level = level;
    }

    @Override
    public Void call() {
        try {
            LevelUtil.playNarrationAudio(level);
            LevelUtil.playPromptChoices(level);
        } catch (Exception e) {
            System.err.println("[OUTPUT THREAD] Audio playback error: " + e.getMessage());
        } finally {
            System.out.println("[OUTPUT THREAD] Playback finished.");
        }
        return null;
    }
}
