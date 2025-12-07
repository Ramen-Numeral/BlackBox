package game.tasks;

import game.audioUtil.audioIn.AudioInputEvent;
import game.audioUtil.audioIn.CaptureAudio;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;

public class RecorderTask implements Runnable {

    private final Deque<byte[]> preBuff;
    private final CompletableFuture<String> nxtCommand;

    public RecorderTask(CompletableFuture<String> nxtCommand, Deque<byte[]> preBuff){
        this.nxtCommand = nxtCommand;
        this.preBuff = preBuff;
    }

    @Override
    public void run() {
        try {
            System.out.println("[Recorder] Starting recording. Pre-buffer size: " + preBuff.size());
            byte[] recorded = CaptureAudio.captureUserAudio(preBuff);
            System.out.println("[Recorder] Recording finished, total bytes: " + recorded.length);

            AudioInputEvent userAud = new AudioInputEvent(recorded);
            String nxt = userAud.matchCommand();
            System.out.println("[Recorder] AudioInputEvent result: " + nxt);
            nxtCommand.complete(nxt);
        } catch (LineUnavailableException | IOException e) {
            System.err.println("[Recorder] Error: " + e.getMessage());
            nxtCommand.completeExceptionally(e);
        }
    }
}
