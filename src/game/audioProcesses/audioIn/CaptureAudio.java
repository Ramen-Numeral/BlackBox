package game.audioProcesses.audioIn;

import javax.sound.sampled.*;
import java.io.*;

public class CaptureAudio {

    private static final float SAMPLE_RATE = 16000.0f;
    private static final int SAMPLE_SIZE = 16;
    private static final int BUFFER_SIZE = 1024;
    private static final int SILENCE_THRESHOLD = 200; // amplitude
    private static final int SILENCE_LIMIT = 50; // consecutive silent buffers
    private static final int TIMEOUT_SECONDS = 45;

    /** wrapper fn for audio capture process**/
    public static byte[] captureUserAudio() throws LineUnavailableException, IOException {
        return record(setupLine());
    }

    /** Setup and return a ready-to-record line */
    public static TargetDataLine setupLine() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);
        return line;
    }

    /** Record audio until silence or timeout, return raw bytes */
    public static byte[] record(TargetDataLine line) throws IOException {
        line.start();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int silentCount = 0;
        boolean speechDetected = false;
        long startTime = System.currentTimeMillis();

        while (true) {
            int bytesRead = line.read(buffer, 0, buffer.length);
            if (bytesRead > 0) out.write(buffer, 0, bytesRead);

            // check amplitude for silence
            boolean silent = true;
            try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buffer, 0, bytesRead))) {
                while (dis.available() >= 2) { // make sure 2 bytes remain
                    short sample = dis.readShort();
                    if (Math.abs(sample) > SILENCE_THRESHOLD) {
                        silent = false;
                        speechDetected = true;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (speechDetected) {
                if (silent) silentCount++;
                else silentCount = 0;
                if (silentCount > SILENCE_LIMIT) break;
            }

            // timeout
            if ((System.currentTimeMillis() - startTime) / 1000 >= TIMEOUT_SECONDS) break;
        }

        line.stop();
        line.close();
        return out.toByteArray();
    }
}
//TODO make sure to handle in the game logic if there is a timeout to repeat the commands
//Do something with the threading so the user can interupt the output as the options are being read out.