package game.tasks;

import game.audioUtil.audioIn.CaptureAudio;

import javax.sound.sampled.TargetDataLine;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioServiceTask implements Runnable {

    private static final int BUFFER_SIZE = 1024;

    private final Deque<byte[]> sharedPreBuffer;
    private final int maxBufferSize;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public AudioServiceTask(Deque<byte[]> sharedPreBuffer, int maxBufferSize) {
        this.sharedPreBuffer = sharedPreBuffer;
        this.maxBufferSize = maxBufferSize;
    }


    @Override
    public void run() {
        TargetDataLine line = null;
        try {
            line = CaptureAudio.setupLine();
            line.start();
            byte[] buffer = new byte[BUFFER_SIZE];
            System.out.println("[AudioService] Started audio capture thread.");

            while (running.get()) {
                int bytesRead = line.read(buffer, 0, buffer.length);
                if (bytesRead <= 0) continue;

                byte[] chunk = new byte[bytesRead];
                System.arraycopy(buffer, 0, chunk, 0, bytesRead);

                synchronized (sharedPreBuffer) {
                    sharedPreBuffer.addLast(chunk);
                    if (sharedPreBuffer.size() > maxBufferSize) {
                        sharedPreBuffer.removeFirst();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[AudioService] Error: " + e.getMessage());
        } finally {
            if (line != null) {
                line.stop();
                line.close();
            }
            System.out.println("[AudioService] Audio capture thread stopped.");
        }
    }
}
