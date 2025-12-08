package game.audioUtil.audioIn;

import game.stateRoutines.envsetup.SetEnv;
import game.audioUtil.audioOut.*;
import javax.sound.sampled.*;
import java.io.*;
import java.util.stream.IntStream;
import java.util.Deque;

//utilities to record audio input
public class CaptureAudio {

    private static final float SAMPLE_RATE = 16000.0f;
    private static final int SAMPLE_SIZE = 16;
    private static final int BUFFER_SIZE = 1024;
    private static final double SILENCE_THRESHOLD_DB = -2.0; // decibels
    private static final int TIMEOUT_SECONDS = 8;
    private static final double SILENCE_SECONDS = 2.5; // 4 seconds of silence

//recording wrapper
    public synchronized static byte[] captureUserAudio(Deque<byte[]> preBuffer) throws LineUnavailableException, IOException {
            return record(preBuffer);
    }

    //calculate decibels of a sample of audio
    public static double calculateDecibels(byte[] audioData) {
        double meanSquare = IntStream.range(0, audioData.length / 2)
                .mapToDouble(i -> {
                    int sample = (audioData[2 * i + 1] << 8) | (audioData[2 * i] & 0xFF);
                    return sample * sample;
                })
                .average()
                .orElse(0.0);

        double rms = Math.sqrt(meanSquare);
        return 20 * Math.log10(rms / 32768.0);
    }

    //set mic line so always consistent across audio fns
    public static TargetDataLine setupLine() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);
        return line;
    }


    //recording times out on extended silence or a timer if that is not met
    private static byte[] record(Deque<byte[]> preBuffer) throws IOException, LineUnavailableException {
        try (TargetDataLine line = setupLine()) {
            line.start();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];

            // --- prepend prebuffer ---
            if (preBuffer != null) {
                for (byte[] chunk : preBuffer) {
                    out.write(chunk);
                }
            }

            long silenceStart = -1;
            long startTime = System.currentTimeMillis();
            AudioFormat format = line.getFormat();

            while (true) {
                int bytesRead = line.read(buffer, 0, buffer.length);
                if (bytesRead <= 0) continue;

                out.write(buffer, 0, bytesRead);

                // calculate decibels for actual bytes read
                byte[] chunkCopy = new byte[bytesRead];
                System.arraycopy(buffer, 0, chunkCopy, 0, bytesRead);
                double db = calculateDecibels(chunkCopy);

                boolean silent = db < SILENCE_THRESHOLD_DB;

                if (!silent) {
                    silenceStart = -1; // reset silence timer if sound detected
                } else {
                    if (silenceStart < 0) silenceStart = System.currentTimeMillis();
                    long silentDuration = System.currentTimeMillis() - silenceStart;
                    if (silentDuration >= SILENCE_SECONDS * 1000 && out.size() > (preBuffer != null ? preBuffer.size() * BUFFER_SIZE : 0)) {
                        break;
                    }
                }

                // Timeout safety
                if (System.currentTimeMillis() - startTime >= TIMEOUT_SECONDS * 1000) break;
            }

            line.stop();
            byte[] audioBytes = out.toByteArray();

            // write to .wav to be sent to whisper (each input overwrites)
            String outputPath = SetEnv.get("USER_INPUT_FILE");
            try (ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes)) {
                AudioInputStream ais = new AudioInputStream(bais, format, audioBytes.length / format.getFrameSize());
                File wavFile = new File(outputPath);
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
            } catch (Exception e) {
                throw new IOException("Failed to write WAV file: " + e.getMessage(), e);
            }

            return audioBytes;
        }
    }

}