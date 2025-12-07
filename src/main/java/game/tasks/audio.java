/*package game.tasks.audio;

import game.stateRoutines.envsetup.SetEnv;
import game.tasks.AudioServiceTask;
import game.tasks.ListenerTask;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public class audio {

    public static void main(String[] args) throws InterruptedException {
       SetEnv.load();
        System.out.println("[MAIN] Starting audio test...");

        // Shared pre-buffer between AudioService and Listener
        Deque<byte[]> sharedPreBuffer = new ArrayDeque<>();

        // Command queue for completed recordings
        BlockingQueue<CompletableFuture<String>> commandQueue = new LinkedBlockingQueue<>();

        // --- Start AudioService thread ---
        AudioServiceTask audioService = new AudioServiceTask(sharedPreBuffer, 50);
        Thread audioServiceThread = new Thread(audioService, "AudioServiceThread");
        audioServiceThread.start();
        System.out.println("[MAIN] AudioServiceThread started.");

        // --- Start Listener thread ---
        ListenerTask listener = new ListenerTask(commandQueue, sharedPreBuffer);
        Thread listenerThread = new Thread(listener, "ListenerThread");
        listenerThread.start();
        System.out.println("[MAIN] ListenerThread started. Speak something to trigger recording...");

        // --- Main loop: wait for commands ---
        while (true) {
            CompletableFuture<String> future = commandQueue.take();
            try {
                String result = future.get();
                System.out.println("[MAIN] Transcription result: " + result);
            } catch (Exception e) {
                System.err.println("[MAIN] Failed to get transcription: " + e.getMessage());
            }
        }

        // To stop cleanly (optional):
        // audioService.stop();
        // listenerThread.interrupt();
        // audioServiceThread.join();
        // listenerThread.join();
    }
}


 */