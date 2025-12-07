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

    private final BlockingQueue<String>GUITextQueue;
    private final BlockingQueue<String> audioQueue;
    public AudioOutTask(BlockingQueue<String> audioInterruptQueue, BlockingQueue<String>GUITextQueue) {
        this.audioQueue = audioInterruptQueue;
        this.GUITextQueue = GUITextQueue;
    }


    @Override
    public void run() {
        try {
            while(true) {
                GameLevel level = WorldMap.getLevel((audioQueue.take()));
                System.out.println("[OUTPUT THREAD] Starting playback for level: " + level.getCommand());
                String guiTxt = level.getNarrationText();

                GUITextQueue.offer(guiTxt);

                // Play narration in chunks so we can check for interruption
                LevelUtil.playNarrationAudio(level);
                audioQueue.clear();
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

