package game.tasks;

import game.commandUtil.CommandUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Callable task for executing commands from recorded audio.
 * Only forwards "user input error" to the audio queue after 5 consecutive failures.
 */
public class CommandTask implements Callable<Void> {

    private final BlockingQueue<CompletableFuture<String>> commandQueue;
    private final BlockingQueue<String> audioQueue;
    private String lastPlayed = "";
    private static int errorCount = 0;
    private static final int MAX_ERROR_COUNT = 10;

    public CommandTask(BlockingQueue<CompletableFuture<String>> commandQueue,
                       BlockingQueue<String> audioQueue) {
        this.commandQueue = commandQueue;
        this.audioQueue = audioQueue;
    }

    @Override
    public Void call() {
        try {
            // Wait for the next recorded command
            CompletableFuture<String> future = commandQueue.take();
            String command = future.get(); // blocks until recording finishes

            System.out.println("[COMMAND THREAD] Received command: " + command);

            // Check if command is invalid
            if (isInvalidCommand(command)) {
                errorCount++;
                System.out.println("[COMMAND THREAD] Error detected. Current streak: " + errorCount);
                if (errorCount >= MAX_ERROR_COUNT) {
                    System.out.println("[COMMAND THREAD] Max error count reached. Sending user input error.");
                    if (lastPlayed != null && !lastPlayed.isEmpty()) {
                        audioQueue.offer(lastPlayed); // repeat previous valid command
                    } else {
                        audioQueue.offer("start a new game"); // fallback
                    }
                }
                return null; // do not process invalid command
            }

            // Valid command: reset error streak
            errorCount = 0;

            // Process valid command
            System.out.println("[COMMAND THREAD] Processing command: " + command);
            String audioKey = CommandUtil.runCommand(command);

            System.out.println("[COMMAND THREAD] command returned from processing " + audioKey);
            audioQueue.clear();
            audioQueue.offer(audioKey);


            System.out.println("[COMMAND THREAD] ouffored to the audio queue " + audioQueue.toString());

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private boolean isInvalidCommand(String str) {
        return str == null || str.isEmpty() || str.equalsIgnoreCase("error");
    }
}
