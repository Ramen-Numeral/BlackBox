package game.tasks;

import game.audioUtil.audioIn.CaptureAudio;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ListenerTask implements Runnable {

    private static final double THRESHOLD_DB = -7;
    private static final int REQUIRED_CHUNKS = 2;          // number of chunks to trigger
    private static final long SAMPLE_INTERVAL_MS = 100;
    private static final long MAX_PAUSE_MS = 2000;         // max pause allowed between chunks
    private static final int MAX_PRE_BUFFER = REQUIRED_CHUNKS + 40;

    private final BlockingQueue<CompletableFuture<String>> commandQueue;
    private final Deque<byte[]> preBuffer = new ArrayDeque<>();
    private Thread currentRecorder;
    private final Object recorderLock = new Object();

    private int aboveThresholdCount = 0;
    private long lastAboveThresholdTime = -1;

    public ListenerTask(BlockingQueue<CompletableFuture<String>> commandQueue) {
        this.commandQueue = commandQueue;
    }

    @Override
    public void run() {
        System.out.println("[LISTENER] Monitoring for speech...");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                processAudioChunk();
                Thread.sleep(SAMPLE_INTERVAL_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processAudioChunk() throws Exception {
        byte[] chunk = CaptureAudio.captureAudioChunk();
        double db = CaptureAudio.calculateDecibels(chunk);

        System.out.printf("[DEBUG] Chunk dB: %.2f%n", db);

        // Maintain pre-buffer
        preBuffer.addLast(chunk);
        if (preBuffer.size() > MAX_PRE_BUFFER) preBuffer.removeFirst();

        checkThresholdAndSpawnRecorder(db);
    }

    private void checkThresholdAndSpawnRecorder(double db) throws InterruptedException, ExecutionException {
        long now = System.currentTimeMillis();

        // Reset count if last above-threshold chunk was too long ago
        if (lastAboveThresholdTime > 0 && now - lastAboveThresholdTime > MAX_PAUSE_MS) {
            System.out.println("[DEBUG] Pause too long. Count reset.");
            aboveThresholdCount = 0;
        }

        if (db > THRESHOLD_DB) {
            aboveThresholdCount++;
            System.out.printf("[DEBUG] Above threshold count: %d%n", aboveThresholdCount);

            if (aboveThresholdCount >= REQUIRED_CHUNKS) {
                System.out.println("[DEBUG] Threshold sustained. Spawning recorder...");
                spawnRecorder();
                aboveThresholdCount = 0;
                lastAboveThresholdTime = -1;
            }
        }
    }

    private void spawnRecorder() throws InterruptedException, ExecutionException {
        synchronized (recorderLock) {
            if (currentRecorder != null && currentRecorder.isAlive()) {
                System.out.println("[DEBUG] Recorder already running. Skipping spawn.");
                return;
            }

            CompletableFuture<String> audioFuture = new CompletableFuture<>();
            Deque<byte[]> bufferSnapshot = new ArrayDeque<>(preBuffer);
            if(bufferSnapshot.isEmpty()) return; // garbage buffer passed from before thread slept/while recorder was running
            currentRecorder = new Thread(new RecorderTask(audioFuture, bufferSnapshot));
            currentRecorder.start();
            commandQueue.offer(audioFuture);
            String result = audioFuture.get(); //dummy line to block until recorder finishes
            preBuffer.clear();
          //  Thread.currentThread().sleep(5000); //put thread to sleep so the game can continue for a bit and can't be spammed with command breaking narrative flow

            System.out.println("[DEBUG] Recorder done. Pre-buffer cleared.");
        }
    }
}
