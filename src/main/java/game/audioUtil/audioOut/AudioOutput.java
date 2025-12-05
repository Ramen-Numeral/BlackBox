package game.audioUtil.audioOut;

import game.api.utilityJSON.AWSUtil;
import game.stateRoutines.envsetup.SetEnv;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioOutput {


    public static AudioInputStream getAudioStream(String filePath) throws IOException, UnsupportedAudioFileException {
        File audioFile = new File(filePath);
        return AudioSystem.getAudioInputStream(audioFile);
    }



    public static void playStream(AudioInputStream audioStream) throws LineUnavailableException, IOException {
        AudioFormat format = audioStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        try (SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info)) {
            audioLine.open(format);
            audioLine.start();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = audioStream.read(buffer)) != -1) {
                audioLine.write(buffer, 0, bytesRead);
            }

            audioLine.drain();
        }
    }


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

                // Ensure all remaining audio in the line's internal buffer is played
                audioLine.drain();

                // Add a small sleep to make sure the last few frames finish
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

    /** Helper: play byte array in chunks and check stopFlag between chunks */
    public static void playByteArrayInterruptible(byte[] audio, AtomicBoolean stopFlag) throws Exception {
        int chunkSize = 1024; // bytes per chunk
        for (int pos = 0; pos < audio.length; pos += chunkSize) {
            if (stopFlag.get()) return; // stop immediately
            int len = Math.min(chunkSize, audio.length - pos);
            byte[] chunk = new byte[len];
            System.arraycopy(audio, pos, chunk, 0, len);
            AudioOutput.playByteArray(chunk); // blocking playback of chunk
        }
    }


    public static void main(String[] args) {
        try {
            // Load .env variables
            SetEnv.load(".env");

            // Text to convert to speech
            String text = "Hello! This is a test of in-memory playback from Polly.";

            // Get audio bytes from Polly
            byte[] audioBytes = AWSUtil.pollyParseResponse(AWSUtil.postPolly(text));

            // Play audio directly from byte array
            AudioOutput.playByteArray(audioBytes);

            System.out.println("Playback complete.");

        } catch (IOException e) {
            System.err.println("Error during Polly audio retrieval: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Playback error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}