package game.tasks;

import game.audioProcesses.audioIn.CaptureAudio;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.*;

public class ListenerTask implements Runnable {

    private final ExecutorService executor;
    private final BlockingQueue<String> commandQueue;
    private final Deque<byte[]> recentChunks = new ArrayDeque<>();
    private long lastTriggerTime = 0;
    private Future<?> currentAudioIn = null;

    public ListenerTask(ExecutorService executor, BlockingQueue<String> commandQueue) {
        this.executor = executor;
        this.commandQueue = commandQueue;
    }

    @Override
    public void run() {
        System.out.println("[LISTENER] Monitoring for speech...");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                byte[] chunk = CaptureAudio.captureAudioChunk();
                double db = CaptureAudio.calculateDecibels(chunk);

                recentChunks.addLast(chunk);
                if (recentChunks.size() > ThreadConfig.MAX_CHUNKS) recentChunks.removeFirst();

                long now = System.currentTimeMillis();
                if (db > ThreadConfig.THRESHOLD_DB &&
                        now - lastTriggerTime > ThreadConfig.DEBOUNCE_MS &&
                        (currentAudioIn == null || currentAudioIn.isDone())) {

                    lastTriggerTime = now;
                    AudioInTask audioInTask = new AudioInTask(new ArrayDeque<>(recentChunks));

                    currentAudioIn = executor.submit(() -> {
                        try {
                            String matchedCommand = audioInTask.call();
                            if (matchedCommand != null) commandQueue.put(matchedCommand);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }

                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
