/*package game.tasks;

import game.audioUtil.audioIn.CaptureAudio;
import game.audioUtil.audioOut.AudioOutput;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;

public class TestRecorderMain {

    private static final double THRESHOLD_DB = -6;
    private static final int MAX_PRE_BUFFER = 20; // enough to cover 1.5 sec
    private static final long MIN_TRIGGER_DURATION_MS = 1500;
    private static final long SAMPLE_INTERVAL_MS = 100;

    public static void main(String[] args) {
        Deque<byte[]> preBuffer = new ArrayDeque<>();
        Thread currentRecorder = null;
        Object recorderLock = new Object();
        long aboveThresholdStart = -1;

        System.out.println("[TEST] Starting audio test. Speak into your mic...");

        while (true) {
            try {
                byte[] chunk = CaptureAudio.captureAudioChunk();
                double db = CaptureAudio.calculateDecibels(chunk);

                // maintain pre-buffer
                preBuffer.addLast(chunk);
                if (preBuffer.size() > MAX_PRE_BUFFER) preBuffer.removeFirst();

                long now = System.currentTimeMillis();

                if (db > THRESHOLD_DB) {
                    if (aboveThresholdStart < 0) aboveThresholdStart = now;
                    else if (now - aboveThresholdStart >= MIN_TRIGGER_DURATION_MS) {
                        synchronized (recorderLock) {
                            if (currentRecorder == null || !currentRecorder.isAlive()) {
                                CompletableFuture<byte[]> audioFuture = new CompletableFuture<>();

                                currentRecorder = new Thread(new RecorderTask(audioFuture, preBuffer));
                                currentRecorder.start();

                                // Play audio when recorder finishes
                                audioFuture.thenAccept(AudioOutput::playByteArray);

                                preBuffer.clear();
                            }
                        }
                        aboveThresholdStart = -1;
                    }
                } else {
                    aboveThresholdStart = -1;
                }

                Thread.sleep(SAMPLE_INTERVAL_MS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
*/