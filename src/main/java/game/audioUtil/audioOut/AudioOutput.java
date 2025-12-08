package game.audioUtil.audioOut;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

//utility to help audio out thread output to speakers fr byte array
public class AudioOutput {

    public static void playByteArray(byte[] audioData) {
        // Polly default PCM: 16 kHz, 16-bit, mono, little-endian
        AudioFormat format = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                16000,   // sample rate
                16,      // sample size in bits
                1,       // channels
                2,       // frame size = bytesPerSample * channels
                16000,   // frame rate = sample rate
                false    // little-endian
        );

        try (ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
             AudioInputStream audioStream = new AudioInputStream(bais, format, audioData.length / format.getFrameSize())) {

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            try (SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info)) {
                audioLine.open(format);
                audioLine.start();

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = audioStream.read(buffer)) != -1) {
                    audioLine.write(buffer, 0, bytesRead);
                }

                // ensure all remaining audio in the line's internal buffer is played
                audioLine.drain();

                // sleep to make sure the last few frames finish
                Thread.sleep(100);  // 100 ms buffer

            } catch (LineUnavailableException e) {
                System.err.println("Audio line unavailable: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("I/O error during playback: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }



}