package game.audioUtil.audioIn;

import game.audioUtil.audioOut.AudioOutput;
import game.stateRoutines.envsetup.SetEnv;

import javax.sound.sampled.*;
import java.io.*;
import java.util.stream.IntStream;

public class CaptureAudio {

    private static final float SAMPLE_RATE = 16000.0f;
    private static final int SAMPLE_SIZE = 16;
    private static final int BUFFER_SIZE = 1024;
    private static final int SILENCE_THRESHOLD = 5000; // amplitude
    private static final int SILENCE_LIMIT = 3;        // consecutive silent buffers before stopping
    private static final int TIMEOUT_SECONDS = 5;

    /** Capture a small chunk for threshold detection */
    public static byte[] captureAudioChunk() throws LineUnavailableException {
        try (TargetDataLine line = setupLine()) {
            line.start();
            byte[] buffer = new byte[BUFFER_SIZE];
            line.read(buffer, 0, buffer.length);
            line.stop();
            return buffer;
        }
    }

    /** Capture full user audio until silence or timeout */
    public static byte[] captureUserAudio() throws LineUnavailableException, IOException {
        try (TargetDataLine line = setupLine()) {
            return record(line);
        }
    }

    /** Calculate decibel level of a buffer */
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

    /** Setup the mic line */
    public static TargetDataLine setupLine() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);
        return line;
    }

    /** Record until silence or timeout */
    private static byte[] record(TargetDataLine line) throws IOException {
        line.start();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int silentCount = 0;
        boolean speechDetected = false;
        long startTime = System.currentTimeMillis();

        AudioFormat format = line.getFormat();

        while (true) {
            int bytesRead = line.read(buffer, 0, buffer.length);
            if (bytesRead > 0) out.write(buffer, 0, bytesRead);

            // RMS for this buffer
            double rms = 0;
            for (int i = 0; i < bytesRead / 2; i++) {
                int sample = (buffer[2 * i + 1] << 8) | (buffer[2 * i] & 0xFF);
                rms += sample * sample;
            }
            rms = Math.sqrt(rms / (bytesRead / 2));

            boolean silent = rms < SILENCE_THRESHOLD;

            if (speechDetected) {
                System.out.println("speech happening");
                silentCount = silent ? silentCount + 1 : 0;
                if (silentCount >= SILENCE_LIMIT) break;
            } else if (!silent) {
                speechDetected = true;
            }

            // Safety timeout
            if ((System.currentTimeMillis() - startTime) / 1000 >= TIMEOUT_SECONDS) break;
        }

        line.stop();
        byte[] audioBytes = out.toByteArray();

        // Write to .wav file
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

    public static void main(String[] args) {
        SetEnv.load(".env");
        System.out.println("Starting microphone test. Speak something...");

        try {
            byte[] audioData = CaptureAudio.captureUserAudio();
            System.out.println("Captured " + audioData.length + " bytes of audio.");

            AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, 1, true, true);
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
