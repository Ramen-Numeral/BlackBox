package game.userAudio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioOutput {

    /** play wrapper */
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

    /** get the stream */
    public static AudioInputStream getAudioStream(String filePath) throws IOException, UnsupportedAudioFileException {
        File audioFile = new File(filePath);
        return AudioSystem.getAudioInputStream(audioFile);
    }

    /** play a stream*/
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

            audioLine.drain(); // finish playback
        }
    }

}
