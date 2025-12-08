package game.tasks;

import game.gameUtil.objs.GameLevel;
import game.gameUtil.helpers.LevelUtil;
import game.gameUtil.objs.WorldMap;

import java.util.concurrent.BlockingQueue;

//accepts a task from the command thread, then plays back the audio, communicates with gui
public class AudioOutTask implements Runnable {

    private String lastLevel;

    private final BlockingQueue<String>GUITextQueue;
    private final BlockingQueue<String> audioQueue;
    public AudioOutTask(BlockingQueue<String> audioInterruptQueue, BlockingQueue<String>GUITextQueue) {
        this.audioQueue = audioInterruptQueue;
        this.GUITextQueue = GUITextQueue;
        this.lastLevel = "";
    }


    @Override
    public void run() {
        try {
            while(true) {
                String command = audioQueue.take();

                System.out.println("Audio out took from audio queue " + command);
                //audioQueue.clear();
                GameLevel level = WorldMap.getLevel(command);
                String guiTxt = level.getNarrationText();
                System.out.println("guiTxt put into the queue for output");
                //put narration text on gui queue for output
                GUITextQueue.offer(guiTxt);
                System.out.println("Audio running level " + level.toString());

                //if there's a user input error, play that file and the previous one
                //else continue and set the last played to the command for future repetition
                if(command.equals("user input error")){
                    audioQueue.offer(lastLevel);
                }
                    lastLevel = command;
                    level.setPlayed(true);


                System.out.println("[OUTPUT THREAD] Starting playback for level: " + level.getCommand());
                System.out.println("Putting GUI text on queue ");


                LevelUtil.playNarrationAudio(level);
                //introduce the choices
                if(!command.equals("load sequence")) {
                    LevelUtil.playNarrationAudio(WorldMap.getLevel("command intro"));
                }
                //play the choices
                LevelUtil.playPromptChoices(level);

            }
        } catch (Exception e) {
            System.err.println("[OUTPUT THREAD] Audio playback error: " + e.getMessage());
        } finally {
            System.out.println("[OUTPUT THREAD] Playback finished.");
        }
    }
}


