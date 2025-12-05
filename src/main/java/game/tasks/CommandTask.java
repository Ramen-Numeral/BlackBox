package game.tasks;

import game.commandUtil.CommandUtil;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * Callable task for executing commands from recorded audio.
 */
public class CommandTask implements Callable<Void> {

    private final BlockingQueue<CompletableFuture<String>> commandQueue;
    private final BlockingQueue<String> audioQueue; // optional: forward key to audio output

    public CommandTask(BlockingQueue<CompletableFuture<String>> commandQueue,
                       BlockingQueue<String> audioQueue) {
        this.commandQueue = commandQueue;
        this.audioQueue = audioQueue;
    }

    @Override
    public Void call() {
        try {
            while (true) {
                // Wait for the next recorded command
                CompletableFuture<String> future = commandQueue.take();
                String command = future.get(); // blocks until recording finishes

                System.out.println("[COMMAND THREAD] Processing command: " + command);

                // Execute system routines or get key for audio output
                String audioKey = CommandUtil.runCommand(command);

                // Forward key to audio output if necessary
                if (audioQueue != null && audioKey != null) {
                    audioQueue.offer(audioKey);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
