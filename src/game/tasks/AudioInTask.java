package game.tasks;

import game.audioUtil.audioIn.AudioInputEvent;
import game.audioUtil.audioIn.CaptureAudio;

import java.util.Deque;
import java.util.concurrent.Callable;

public class AudioInTask implements Callable<String> {

    public static final double THRESHOLD_DB = -40;
    public static final int MAX_CHUNKS = 5;
    public static final long DEBOUNCE_MS = 500;
    public static final int AUDIO_BUFFER_SIZE = 1024;
    public static final int AUDIO_SAMPLE_RATE = 16000;
    public static final int AUDIO_SAMPLE_SIZE = 16;
    private final Deque<byte[]> recentChunks;

    public AudioInTask(Deque<byte[]> recentChunks) {
        this.recentChunks = recentChunks;
    }

    @Override
    public String call() throws Exception {
        System.out.println("[AUDIO IN TASK] Capturing full audio...");

        // Capture full audio
        byte[] fullAudio = CaptureAudio.captureUserAudio();

        // Prepend recent chunks
        byte[] combinedAudio = prependChunks(fullAudio, recentChunks);

        // Match command
        AudioInputEvent event = new AudioInputEvent(combinedAudio);
        String matchedCommand = event.matchCommand();

        System.out.printf("[AUDIO IN TASK] Transcribed: '%s', Matched command: '%s'%n",
                event.getTranscript(), matchedCommand);

        return matchedCommand;
    }

    private byte[] prependChunks(byte[] fullAudio, Deque<byte[]> chunks) {
        int totalLength = fullAudio.length + chunks.stream().mapToInt(c -> c.length).sum();
        byte[] combined = new byte[totalLength];
        int pos = 0;
        for (byte[] c : chunks) {
            System.arraycopy(c, 0, combined, pos, c.length);
            pos += c.length;
        }
        System.arraycopy(fullAudio, 0, combined, pos, fullAudio.length);
        return combined;
    }
}
