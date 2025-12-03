package game.audioProcesses.audioOut;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class AudioOutput {

    public static void playFile(String filePath) {
        try (AudioInputStream stream = getAudioStream(filePath)) {
            playStream(stream);
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio file format: " + filePath);
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable for playback");
        } catch (IOException e) {
            System.err.println("Error playing audio file: " + e.getMessage());
        }
    }

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
        try (ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
             AudioInputStream stream = AudioSystem.getAudioInputStream(bais)) {

            playStream(stream);

        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio format in byte array: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable for playback: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O Error during byte array playback: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Audio playback test starting...");

        String audioFile = "test.wav";

        try {
            AudioOutput.playFile(audioFile);
            System.out.println("Finished playing file: " + audioFile);


        } catch (Exception e) {
            System.err.println("Error during playback: " + e.getMessage());
            e.printStackTrace();
        }
    }

}