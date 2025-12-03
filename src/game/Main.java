package game;

import game.tasks.*;

import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(4);        // Listener + command processor
        ExecutorService audioExecutor = Executors.newSingleThreadExecutor(); // Serial audio playback
        BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();

        // Submit main tasks
        executor.submit(new ListenerTask(executor, commandQueue));
        executor.submit(new CommandProcessorTask(executor, audioExecutor, commandQueue));

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[MAIN] Shutting down executors...");
            executor.shutdownNow();
            audioExecutor.shutdownNow();
        }));
    }
}
