package game.tasks;

import game.audioUtil.audioIn.CaptureAudio;

import javax.sound.sampled.TargetDataLine;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutionException;

public class AmbientDBTask implements Runnable {

    private static final long SAMPLE_INTERVAL_MS = 5;
    private static final int MAX_BUFFER_SIZE = 100;

    private final Deque<byte[]> preBuffer;

    public AmbientDBTask() {
        this.preBuffer = new ArrayDeque<>();
    }

    @Override
    public void run() {
        TargetDataLine line = null;
        try {
            // Setup line once instead of inside the loop
            line = CaptureAudio.setupLine();
            line.start();
            byte[] buffer = new byte[1024];

            while (!Thread.currentThread().isInterrupted()) {
                int bytesRead = line.read(buffer, 0, buffer.length);
                if (bytesRead <= 0) continue;

                byte[] chunk = new byte[bytesRead];
                System.arraycopy(buffer, 0, chunk, 0, bytesRead);

                // Keep the preBuffer thread-safe
                synchronized (preBuffer) {
                    preBuffer.addLast(chunk);
                    if (preBuffer.size() > MAX_BUFFER_SIZE) {
                        preBuffer.removeFirst();
                    }
                }

                Thread.sleep(SAMPLE_INTERVAL_MS);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (line != null) line.close();
        }

        System.out.println("[AmbientDBTask] Listener thread exiting.");
    }


    public double getDB() throws InterruptedException, ExecutionException {
        byte[] latestChunk;
        synchronized (preBuffer) {
            if (preBuffer.isEmpty()) return 0;
            latestChunk = preBuffer.peekLast();
        }
        return CaptureAudio.calculateDecibels(latestChunk);
    }
}
