package game.tasks;

import game.gameUtil.objs.GameLevel;
import game.gameUtil.helpers.LevelUtil;
import game.gameUtil.objs.WorldMap;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Runnable task for playing audio output for a given GameLevel.
 * Interruptible: if a new command arrives, playback will stop immediately.
 */
public class AudioOutTask implements Runnable {

    private final AtomicBoolean stopFlag;
    private final BlockingQueue<String> audioInterruptQueue;

    /**
     * @param stopFlag AtomicBoolean used to interrupt playback
     * @param audioInterruptQueue Queue that receives keys from CommandTask
     */
    public AudioOutTask(AtomicBoolean stopFlag, BlockingQueue<String> audioInterruptQueue) {
        this.stopFlag = stopFlag;
        this.audioInterruptQueue = audioInterruptQueue;
    }

    @Override
    public void run() {
        try {
            GameLevel level = WorldMap.getLevel(audioInterruptQueue.take());
            System.out.println("[OUTPUT THREAD] Starting playback for level: " + level.getCommand());

            // Play narration in chunks so we can check for interruption
            LevelUtil.playNarrationAudio(level);
            //TODO add a file that introduces the prompt choices
            LevelUtil.playPromptChoices(level);
        } catch (Exception e) {
            System.err.println("[OUTPUT THREAD] Audio playback error: " + e.getMessage());
        } finally {
            System.out.println("[OUTPUT THREAD] Playback finished.");
        }
    }
}


/*
            if (stopFlag.get()) {
                System.out.println("[OUTPUT THREAD] Playback interrupted before prompt choices.");
                return;
            }

            LevelUtil.playPromptChoices(level, stopFlag);

            if (stopFlag.get()) {
                System.out.println("[OUTPUT THREAD] Playback interrupted during prompt choices.");
            }

 */

