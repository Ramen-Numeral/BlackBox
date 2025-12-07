package game.gameUtil.test;

import game.gameUtil.objs.GameLevel;
import game.audioUtil.audioOut.AudioOutput;
import game.stateRoutines.envsetup.SetEnv;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayInputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameLevelTest {

    public static void main(String[] args) {
        try {
            // Load environment variables
            SetEnv.load();

            // Example level file path (adjust to your actual file)
            String levelPath = "level_texts/start.txt";

            System.out.println("Loading GameLevel from: " + levelPath);
            GameLevel level = new GameLevel(levelPath);

            System.out.println("Narration length: " + level.getNarrationAudio().length);
            System.out.println("Command prompt length: " + level.getCommandPromptAudio().length);

            // Try playByteArray
            System.out.println("Playing narration...");
            AudioOutput.playByteArray(level.getNarrationAudio());
            System.out.println("Playing command prompt...");
            AudioOutput.playByteArray(level.getCommandPromptAudio());

            // Try playStream
            System.out.println("Playing narration via stream...");
            ByteArrayInputStream bais = new ByteArrayInputStream(level.getNarrationAudio());
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            AudioInputStream ais = new AudioInputStream(bais, format, level.getNarrationAudio().length / format.getFrameSize());
            AudioOutput.playStream(ais);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Wrapper for narration
    private static void playNarration(GameLevel level, AtomicBoolean stopFlag) throws Exception {
        byte[] narration = level.getNarrationAudio();
        if (narration != null && narration.length > 0) {
            AudioOutput.playByteArrayInterruptible(narration, stopFlag);
        } else {
            System.out.println("No narration audio found for this level.");
        }
    }

    // Wrapper for prompt choices
    private static void playPromptChoices(GameLevel level, AtomicBoolean stopFlag) throws Exception {
        for (String command : level.getAvailableCommands()) {
            GameLevel choice = game.gameUtil.objs.WorldMap.getLevel(command);
            if (choice != null && !choice.isPlayed()) {
                if (stopFlag.get()) return; // allow interruption
                byte[] audio = choice.getCommandPromptAudio();
                if (audio != null && audio.length > 0) {
                    AudioOutput.playByteArrayInterruptible(audio, stopFlag);
                }
            }
        }
    }
}
