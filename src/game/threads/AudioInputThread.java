package game.threads;

import game.audioProcesses.audioIn.AudioInputEvent;
import game.audioProcesses.audioIn.CaptureAudio;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Worker thread for handling the entire voice input pipeline.
 * Implements Callable<String> because it performs a long-running task
 * and returns the matched command string to the GameEngine via a Future.
 * This class isolates the main game loop from the blocking I/O and network latency.
 */
public class AudioInputThread implements Callable<String> {

    /**
     * Executes the audio capture, processing, and command matching in a dedicated thread.
     * This is the method run by the ExecutorService.
     *
     * @return The matched game command (e.g., "go north").
     * @throws Exception if a critical error (hardware, I/O, API failure) occurs.
     * The GameEngine will catch this exception and handle the fallback logic.
     */
    @Override
    public String call() throws Exception {
        System.out.println("\n[INPUT THREAD] Activated. Waiting for user to speak...");

        byte[] rawAudio;
        try {
            // 1. Capture Audio (BLOCKING I/O, using your CaptureAudio implementation)
            rawAudio = CaptureAudio.captureUserAudio();

        } catch (LineUnavailableException | IOException e) {
            // Re-throw exceptions related to hardware or IO issues
            System.err.println("[INPUT THREAD] FATAL ERROR: Audio Capture Failed: " + e.getMessage());
            throw new Exception("Capture failure", e);
        }

        System.out.println("[INPUT THREAD] Audio captured. Processing input event...");
        try {
            // 2. Process Audio (Triggers blocking API calls for Transcription and Embedding)
            AudioInputEvent inputEvent = new AudioInputEvent(rawAudio);

            // 3. Match Command (Fast, local vector comparison)
            String matchedCommand = inputEvent.matchCommand();

            System.out.printf("[INPUT THREAD] Processing complete. Matched Command: '%s'\n", matchedCommand);
            return matchedCommand;

        } catch (RuntimeException e) {
            // Catches RuntimeException thrown by AudioInputEvent due to API/network failures
            System.err.println("[INPUT THREAD] FATAL ERROR: Failed to process audio input via API: " + e.getMessage());
            throw new Exception("Processing failure", e);
        }
    }
}