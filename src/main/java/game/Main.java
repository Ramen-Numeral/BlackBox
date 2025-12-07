package game;
import game.stateRoutines.StartupRoutine;
import game.tasks.AudioOutTask;
import game.tasks.CommandTask;
import game.tasks.ListenerTask;
import game.tasks.AudioServiceTask;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.*;

import javax.swing.SwingUtilities;

public class Main {


    public static void main(String[] args) throws Exception {

        StartupRoutine.startupRoutine();

        CompletableFuture<Double> dbFuture = new CompletableFuture<>();

        SwingUtilities.invokeLater(() -> {
            new ThresholdGUI(dbFuture);
        });


        // Main thread waits for user input
        double threshold = dbFuture.get();

        System.out.println("[MAIN] User chose threshold: " + threshold);
        System.out.println("[MAIN] Starting audio pipeline...");

        // Queues for pipeline
        BlockingQueue<CompletableFuture<String>> commandQueue = new ArrayBlockingQueue<>(10);
        BlockingQueue<String> audioQueue = new ArrayBlockingQueue<>(10); // existing audio pipeline queue
        BlockingQueue<String> guiQueue = new LinkedBlockingQueue<>(); // new GUI queue for TextOutTask

        // Shared pre-buffer for Listener + AudioService
        Deque<byte[]> sharedPreBuffer = new ArrayDeque<>();

        // --- Start GUI on Swing thread ---
        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI(guiQueue);
            guiQueue.offer("loading...");
        });

        // --- Start AudioOutTask thread ---
        AudioOutTask audioOutTask = new AudioOutTask(audioQueue, guiQueue);
        Thread audioThread = new Thread(audioOutTask, "AudioThread");
        audioThread.start();
        // --- Startup audio ---
        audioQueue.put("start game");

        System.out.println("[MAIN] Starting audio pipeline...");

        // Queues for pipeline

        // --- Start GUI on Swing thread ---
        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI(guiQueue); // GUI now consumes guiQueue
        });

        // --- Startup audio ---


        // --- Start AudioService (fills shared pre-buffer) ---
        AudioServiceTask audioService = new AudioServiceTask(sharedPreBuffer, 50);
        Thread audioServiceThread = new Thread(audioService, "AudioServiceThread");
        audioServiceThread.setDaemon(true);
        audioServiceThread.start();
        System.out.println("[MAIN] AudioServiceThread started.");

        // --- Start Listener thread ---
        ListenerTask listener = new ListenerTask(commandQueue, sharedPreBuffer, threshold);
        Thread listenerThread = new Thread(listener, "ListenerThread");
        listenerThread.start();
        System.out.println("[MAIN] ListenerThread started.");

        // --- Start CommandTask thread pool ---
        ExecutorService commandExecutor = Executors.newSingleThreadExecutor(r -> new Thread(r, "CommandThread"));
        commandExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    CommandTask task = new CommandTask(commandQueue, audioQueue); // unchanged
                    task.call(); // blocks until a command is processed
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("[MAIN] CommandThread started.");
    }
}
