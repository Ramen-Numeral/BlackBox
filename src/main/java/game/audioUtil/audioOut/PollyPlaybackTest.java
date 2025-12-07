package game.gameUtil.test;

import game.audioUtil.audioOut.AudioOutput;
import game.api.utilityJSON.AWSUtil;
import game.stateRoutines.envsetup.SetEnv;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class PollyPlaybackTest {

    public static void main(String[] args) {
        try {
            // Load environment variables
            SetEnv.load();

            // Text to synthesize
            String text = "Hello! This is a test of Polly audio playback using both methods.";

            // Get PCM bytes from AWS Polly
            byte[] audioBytes = AWSUtil.pollyParseResponse(AWSUtil.postPolly(text));
            System.out.println("Audio bytes length: " + audioBytes.length);

            // --- Test 1: playByteArray ---
            System.out.println("Playing audio with playByteArray...");
            AudioOutput.playByteArray(audioBytes);
            System.out.println("playByteArray complete.");

            // --- Test 2: playStream ---
            System.out.println("Playing audio with playStream...");
            try (AudioInputStream ais = new AudioInputStream(
                    new ByteArrayInputStream(audioBytes),
                    new javax.sound.sampled.AudioFormat(
                            16000, 16, 1, true, false
                    ),
                    audioBytes.length / 2 // frame size = 2 bytes
            )) {
                AudioOutput.playStream(ais);
            }
            System.out.println("playStream complete.");

        } catch (IOException | LineUnavailableException e) {
            System.err.println("Playback test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
