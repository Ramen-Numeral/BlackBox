package game.audioProcesses.audioOut;
import game.audioProcesses.audioOut.AudioOutput;

/**
 * Worker thread for handling audio playback asynchronously.
 * Implements Runnable because the task does not return a value to the caller,
 * allowing the GameEngine to submit it and continue immediately.
 * * This thread calls the blocking playback logic in the AudioOutput utility class.
 */
public class AudioOutThread implements Runnable {

    private final byte[] audioBytes;

    /**
     * Initializes the task with the audio data to be played.
     * @param audioBytes The byte array containing the audio data.
     */
    public AudioOutThread(byte[] audioBytes) {
        this.audioBytes = audioBytes;
    }

    /**
     * The run method executes the blocking audio playback operation.
     * This is executed by the ExecutorService in a dedicated worker thread.
     */
    @Override
    public void run() {
        if (audioBytes == null || audioBytes.length == 0) {
            // Quietly exit if there is no audio data
            return;
        }

        System.out.printf("[OUTPUT THREAD] Starting asynchronous playback of %d bytes...\n", audioBytes.length);

        try {
            // Call the user's blocking utility method for playback
            AudioOutput.playByteArray(audioBytes);

        } catch (Exception e) {
            // Catch any unexpected exceptions from the playback process
            System.err.println("[OUTPUT THREAD] Error during audio playback: " + e.getMessage());
        } finally {
            System.out.println("[OUTPUT THREAD] Playback task finished.");
        }
    }
}