package game.tasks;

import game.gameUtil.objs.GameLevel;
import game.gameUtil.helpers.LevelUtil;
import game.gameUtil.objs.WorldMap;

import java.util.concurrent.BlockingQueue;

/**
 * Runnable task for playing audio output for a given GameLevel.
 * Interruptible: if a new command arrives, playback will stop immediately.
 */
public class AudioOutTask implements Runnable {


    private final BlockingQueue<String> audioInterruptQueue;
    public AudioOutTask(BlockingQueue<String> audioInterruptQueue) {
        this.audioInterruptQueue = audioInterruptQueue;
    }


    @Override
    public void run() {
        try {
            while(true) {
                GameLevel level = WorldMap.getLevel((audioInterruptQueue.take()));
                System.out.println("[OUTPUT THREAD] Starting playback for level: " + level.getCommand());

                // Play narration in chunks so we can check for interruption
                LevelUtil.playNarrationAudio(level);
                LevelUtil.playPromptChoices(level);
            }
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

