package game.tasks;

import game.audioUtil.audioIn.CaptureAudio;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

public class ListenerTask implements Runnable {

    private static final double THRESHOLD_DB = -8;   // min volume to trigger speech
    private static final int MAX_PRE_BUFFER = 5;      // number of recent chunks to keep before speech
    private final BlockingQueue<CompletableFuture<String>> commandQueue;
    private final Deque<byte[]> preBuffer = new ArrayDeque<>();
    private Thread currentRecorder;

    public ListenerTask(BlockingQueue<CompletableFuture<String>> commandQueue) {
        this.commandQueue = commandQueue;
    }

    @Override
    public void run() {
        System.out.println("[LISTENER] Monitoring for speech...");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                byte[] chunk = CaptureAudio.captureAudioChunk();
                double db = CaptureAudio.calculateDecibels(chunk);


                // Maintain pre-buffer
                preBuffer.addLast(chunk);
                if (preBuffer.size() > MAX_PRE_BUFFER) preBuffer.removeFirst();
                if (db > THRESHOLD_DB && (currentRecorder == null || !currentRecorder.isAlive())) {

                    CompletableFuture<String> audioFuture = new CompletableFuture<>();

                    currentRecorder = new Thread(new RecorderTask(audioFuture, preBuffer));
                    currentRecorder.start();

                    // Give the future to the command processor
                    commandQueue.offer(audioFuture);

                    preBuffer.clear(); // clear pre-buffer after spawning recorder
                }

                Thread.sleep(10); // prevent busy looping
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
