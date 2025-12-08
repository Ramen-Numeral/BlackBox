package game.tasks;

import game.commandUtil.CommandUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


//class that controls command processing to clear queue of stale commands
//handle failed input before sending to the audio output
public class CommandTask implements Runnable {

    private final BlockingQueue<CompletableFuture<String>> commandQueue; //shared with listener
    private final BlockingQueue<String> audioQueue;
    private String lastPlayed = "";
    private static int errorCount = 0;
    private static final int MAX_ERROR_COUNT = 15;

    public CommandTask(BlockingQueue<CompletableFuture<String>> commandQueue,
                       BlockingQueue<String> audioQueue) {
        this.commandQueue = commandQueue;
        this.audioQueue = audioQueue;
    }

    @Override
    public void run() {
        try {
            // Wait for the next recorded command
            CompletableFuture<String> future = commandQueue.take();
            String command = future.get();

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
                        audioQueue.offer("exit"); // fallback exit to prevent game running infinitely/spam calling api
                    }
                }
                return; // do not process invalid command
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

        return;
    }

    private boolean isInvalidCommand(String str) {
        return str == null || str.isEmpty() || str.equalsIgnoreCase("error");
    }
}
