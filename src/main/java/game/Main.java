package game;

import game.audioUtil.audioOut.AudioOutput;
import game.commandUtil.CommandUtil;
import game.gameUtil.objs.WorldMap;
import game.stateRoutines.StartupRoutine;
import game.tasks.AudioOutTask;
import game.tasks.CommandTask;
import game.tasks.ListenerTask;
import game.tasks.AudioServiceTask;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws Exception {
        StartupRoutine.startupRoutine();
        System.out.println("[MAIN] Starting audio pipeline...");

        // Queues for pipeline
        BlockingQueue<CompletableFuture<String>> commandQueue = new ArrayBlockingQueue<>(10);
        BlockingQueue<String> audioQueue = new ArrayBlockingQueue<>(10);

        // Shared pre-buffer for Listener + AudioService
        Deque<byte[]> sharedPreBuffer = new ArrayDeque<>();

        // --- Startup audio ---
        AudioOutput.playByteArray(WorldMap.getLevel("start game").getNarrationAudio());
        AudioOutput.playByteArray(WorldMap.getLevel("start game").getCommandPromptAudio());

        // --- Start AudioService (fills shared pre-buffer) ---
        AudioServiceTask audioService = new AudioServiceTask(sharedPreBuffer, 50);
        Thread audioServiceThread = new Thread(audioService, "AudioServiceThread");
        audioServiceThread.setDaemon(true);
        audioServiceThread.start();
        System.out.println("[MAIN] AudioServiceThread started.");

        // --- Start Listener thread ---
        ListenerTask listener = new ListenerTask(commandQueue, sharedPreBuffer);
        Thread listenerThread = new Thread(listener, "ListenerThread");
        listenerThread.start();
        System.out.println("[MAIN] ListenerThread started.");

        // --- Start CommandTask thread pool ---
        ExecutorService commandExecutor = Executors.newSingleThreadExecutor(r -> new Thread(r, "CommandThread"));
        commandExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    CommandTask task = new CommandTask(commandQueue, audioQueue);
                    task.call(); // blocks until a command is processed
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("[MAIN] CommandThread started.");

        // --- Start AudioOutTask thread ---
        Thread audioThread = new Thread(() -> {
            AudioOutTask audioOutTask = new AudioOutTask(audioQueue);
            audioOutTask.run(); // blocking loop inside AudioOutTask
        }, "AudioThread");
        audioThread.start();
        System.out.println("[MAIN] AudioThread started.");
    }
}
