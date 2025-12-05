/*package game.tasks;

import game.audioUtil.audioIn.AudioInputEvent;
import game.audioUtil.audioIn.CaptureAudio;
import game.stateRoutines.envsetup.SetEnv;
//TODO use capture user audio so it is automic the chunking method is too choppy only chunk for the listener.
//TODO keep the atomic method in there so it can't get interrupted.
//TODO  break into listener thread and audio in thread so once the audio in thread is engaged it blocks.
//TODO maybe implement a master task queue in main that coordinates the threads? or spawn an uninterruptible thread in
//TODO inside of the listener thread.
//TODO refactor captureUserAudio so that it doesn't listen for a threshold level etc that will be handled in the main
//TODO listener thread. break the loop below up. keep the buffer approach in the listener thread
//TODO if db threshold is reached, spawn the uninterruptible thread. thread stop on user input task created
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.BlockingQueue;

public class MasterAudioTask implements Runnable {

    private static final double THRESHOLD_DB = -6;   // min volume to trigger speech
    private static final double SILENCE_DB = -50;     // volume considered silent
    private static final long SILENCE_MS = 700;       // silence duration to end speech
    private static final int MAX_PRE_BUFFER = 5;      // number of recent chunks to keep before speech

    private final BlockingQueue<String> commandQueue;
    private final Deque<byte[]> preBuffer = new ArrayDeque<>();
    private boolean isRecording = false;
    private long lastLoudTime = 0;

    public MasterAudioTask(BlockingQueue<String> commandQueue) {
        this.commandQueue = commandQueue;
    }

    @Override
    public void run() {
        System.out.println("[LISTENER] Monitoring for speech...");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                byte[] chunk = CaptureAudio.captureAudioChunk();
                double db = CaptureAudio.calculateDecibels(chunk);
                long now = System.currentTimeMillis();

                // Maintain pre-buffer
                preBuffer.addLast(chunk);
                if (preBuffer.size() > MAX_PRE_BUFFER) preBuffer.removeFirst();

                if (db > THRESHOLD_DB) {
                    // Start or continue recording
                    if (!isRecording) {
                        System.out.println("[LISTENER] Speech detected, starting recording...");
                        isRecording = true;
                    }
                    lastLoudTime = now;
                }

                if (isRecording) {
                    // Capture chunks into a full audio buffer until silence
                    Deque<byte[]> recordingBuffer = new ArrayDeque<>(preBuffer);
                    while (true) {
                        byte[] nextChunk = CaptureAudio.captureAudioChunk();
                        double nextDb = CaptureAudio.calculateDecibels(nextChunk);
                        recordingBuffer.addLast(nextChunk);

                        if (nextDb > THRESHOLD_DB) lastLoudTime = System.currentTimeMillis();

                        if (System.currentTimeMillis() - lastLoudTime > SILENCE_MS) break;
                    }

                    // Stop recording
                    isRecording = false;

                    // Combine the audio chunks into a single byte array
                    byte[] fullAudio = combineChunks(recordingBuffer);
                    AudioFormat format = new AudioFormat(16000.0f, 16, 1, true, true);

                    // Write to WAV file
                    String outputPath = SetEnv.get("USER_INPUT_FILE");
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(fullAudio);
                         AudioInputStream ais = new AudioInputStream(bais, format, fullAudio.length / format.getFrameSize())) {
                        File wavFile = new File(outputPath);
                        // Ensure parent directories exist
                        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
                        System.out.println("[LISTENER] WAV file written to: " + wavFile.getAbsolutePath());
                        System.out.println("[LISTENER] Speech ended, processing full audio...");
                       // processAudio(fullAudio);
                        callWhisper(SetEnv.get)

                    } catch (IOException e) {
                        throw new IOException("Failed to write WAV file: " + e.getMessage(), e);
                    }




                    preBuffer.clear(); // reset pre-buffer
                }

                Thread.sleep(10); // prevent busy looping
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] combineChunks(Deque<byte[]> chunks) {
        int totalLength = chunks.stream().mapToInt(c -> c.length).sum();
        byte[] combined = new byte[totalLength];
        int pos = 0;
        for (byte[] c : chunks) {
            System.arraycopy(c, 0, combined, pos, c.length);
            pos += c.length;
        }
        return combined;
    }

    private void processAudio(byte[] audioBytes) {
        try {
            AudioInputEvent event = new AudioInputEvent(audioBytes);
            System.out.println("[AUDIO TASK] AudioInputEvent created: " + event.getTranscript());
            // String matchedCommand = event.matchCommand();
            // if (matchedCommand != null) commandQueue.put(matchedCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}*/
