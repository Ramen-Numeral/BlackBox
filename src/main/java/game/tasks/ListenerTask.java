package game.tasks;

import game.audioUtil.audioIn.CaptureAudio;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//listens for an input trigger
public class ListenerTask implements Runnable {

    private static double THRESHOLD_DB; //passed in by the threshold gui
    private static final int REQUIRED_CHUNKS = 6; //amount of samples above threshold to trigger recorder
    private static final long SAMPLE_INTERVAL_MS = 5; //frequency of sample

    private final BlockingQueue<CompletableFuture<String>> commandQueue;
    private final Deque<byte[]> preBuffer; //shared w audio service and recorder
    private Thread currentRecorder; //recorder spawned inside in order to block listener from starting new input event mid recording
    private final Object recorderLock = new Object();

    private int aboveThresholdCount = 0;
    private long lastAboveThresholdTime = -1;

    public ListenerTask(BlockingQueue<CompletableFuture<String>> commandQueue, Deque<byte[]> sharedPreBuffer, double thresh) {
        this.commandQueue = commandQueue;
        this.preBuffer = sharedPreBuffer;
        THRESHOLD_DB = thresh;
    }

    //runs continuously to check if ambient noise level spikes, signalling user speech
    @Override
    public void run() {
        System.out.println("[Listener] Monitoring for speech...");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                processLatestChunk();
                Thread.sleep(SAMPLE_INTERVAL_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("[Listener] Listener thread exiting.");
    }

    //checks the decibel level of the last bit in the preBuffer
    private void processLatestChunk() throws InterruptedException, ExecutionException {
        byte[] latestChunk;
        synchronized (preBuffer) {
            if (preBuffer.isEmpty()) return;
            latestChunk = preBuffer.peekLast();
        }

        double db = CaptureAudio.calculateDecibels(latestChunk);
        System.out.printf("[Listener] Latest chunk dB: %.2f%n", db);
        checkThresholdAndSpawnRecorder(db);
    }

    //checks that threshold has been passed, resets if too long has happened between last above threshold event
    private void checkThresholdAndSpawnRecorder(double db) throws InterruptedException, ExecutionException {
        long now = System.currentTimeMillis();

        if (lastAboveThresholdTime > 0 && now - lastAboveThresholdTime > 2000) {
            System.out.println("[Listener] Pause too long, resetting count.");
            aboveThresholdCount = 0;
        }

        if (db > THRESHOLD_DB) {
            aboveThresholdCount++;
            System.out.println("[Listener] Above threshold count: " + aboveThresholdCount);

            if (aboveThresholdCount >= REQUIRED_CHUNKS) {
                System.out.println("[Listener] Threshold sustained, spawning recorder...");
                spawnRecorder();
                aboveThresholdCount = 0;
                lastAboveThresholdTime = -1;
            }
        }
    }

    private void spawnRecorder() throws InterruptedException, ExecutionException {
        synchronized (recorderLock) {
            if (currentRecorder != null && currentRecorder.isAlive()) {
                System.out.println("[Listener] Recorder already running, skipping spawn.");
                return;
            }
            CompletableFuture<String> audioFuture = new CompletableFuture<>();
            Deque<byte[]> bufferSnapshot;
            synchronized (preBuffer) {
                bufferSnapshot = new ArrayDeque<>(preBuffer);
                System.out.println("[Listener] Snapshot taken, size: " + bufferSnapshot.size());
            }

            currentRecorder = new Thread(new RecorderTask(audioFuture, bufferSnapshot));
            currentRecorder.start();
            commandQueue.offer(audioFuture); //put the future on the queue for command
            String result = audioFuture.get(); //block until the recorder has finished
            Thread.currentThread().sleep(1000); //if a recording was found sleep
            System.out.println("[Listener] Recorder finished, result: " + result);
        }
    }
}
