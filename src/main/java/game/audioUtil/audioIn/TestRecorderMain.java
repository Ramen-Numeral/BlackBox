package game.audioUtil.audioIn;

import game.audioUtil.audioOut.AudioOutput;
import game.stateRoutines.envsetup.SetEnv;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TestRecorderMain {

    public static void main(String[] args) {
        SetEnv.load(".env");
        System.out.println("Starting microphone test. Speak something...");

        try {
            // Capture user audio until silence is detected
            byte[] audioData = CaptureAudio.captureUserAudio();
            System.out.println("Captured " + audioData.length + " bytes of audio.");

            // Play it back
            AudioFormat format = new AudioFormat(
                    16000.0f,   // sample rate
                    16,         // sample size in bits
                    1,          // channels
                    true,       // signed
                    true        // big endian
            );

            try (AudioInputStream ais = new AudioInputStream(
                    new ByteArrayInputStream(audioData),
                    format,
                    audioData.length / format.getFrameSize()
            )) {
                System.out.println("Playing back captured audio...");
                AudioOutput.playStream(ais);
            }

            System.out.println("Playback complete.");

        } catch (LineUnavailableException | IOException e) {
            System.err.println("Error during capture/playback: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
