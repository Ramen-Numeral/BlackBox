package game.tasks;

import game.audioUtil.audioIn.AudioInputEvent;
import game.audioUtil.audioIn.CaptureAudio;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;

public class RecorderTask implements Runnable {
    private Deque <byte[]> buff;
    private final CompletableFuture<String>nxtCommand;

    public RecorderTask(CompletableFuture<String> nxtCommand, Deque<byte[]> preBuff){
        this.nxtCommand = nxtCommand;
        this.buff = new ArrayDeque<>(preBuff); // copy at start
    }

    public void run(){
        try {
            byte[] userRecording = CaptureAudio.captureUserAudio();
            buff.add(userRecording);
            AudioInputEvent userAud = new AudioInputEvent(combineChunks(buff));

            String nxt = userAud.matchCommand();
            nxtCommand.complete(nxt);
            //match Command will return error. if audio input event returns an error, then command task should not poll it
        } catch (LineUnavailableException | IOException e) {
            nxtCommand.completeExceptionally(e);
            throw new RuntimeException(e);
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

}