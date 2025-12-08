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

        StartupRoutine.startupRoutine(); //loads audio / embeddings so api calls don't have to be made each load

        CompletableFuture<Double> dbFuture = new CompletableFuture<>();

        SwingUtilities.invokeLater(() -> { //gui to get db threshold
            new ThresholdGUI(dbFuture);
        });


        // Main thread waits for user input
        double threshold = dbFuture.get(); //force stop until user selects db input threshold

        System.out.println("[MAIN] User chose threshold: " + threshold);
        System.out.println("[MAIN] Starting audio pipeline...");

        // Queues for pipeline
        BlockingQueue<CompletableFuture<String>> commandQueue = new ArrayBlockingQueue<>(10); //recorder -> command processing
        BlockingQueue<String> audioQueue = new ArrayBlockingQueue<>(10); // audio pipeline queue
        BlockingQueue<String> guiQueue = new LinkedBlockingQueue<>(); //Text output gui
        Deque<byte[]> sharedPreBuffer = new ArrayDeque<>(); //rolling prebuffer to append to audio input to guarantee no loss of input

        // --- Start GUI on Swing thread ---
        SwingUtilities.invokeLater(() -> { // gui that shows the game
            GUI gui = new GUI(guiQueue);
        });


        // --- Startup audio ---
        audioQueue.put("load sequence"); //start the game
        System.out.println("[MAIN] Starting audio pipeline...");


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
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("[MAIN] CommandThread started.");
        AudioOutTask audioOutTask = new AudioOutTask(audioQueue, guiQueue);
        Thread audioThread = new Thread(audioOutTask, "AudioThread");
        audioThread.start();
    }

}
