package game.tasks;

import game.commandUtil.CommandUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Callable task for executing commands from recorded audio.
 */
public class CommandTask implements Callable<Void> {

    private final BlockingQueue<CompletableFuture<String>> commandQueue;
    private final BlockingQueue<String> audioQueue; // optional: forward key to audio output
    private String lastPlayed = "";
    private static int errorCount = 0;
    private static int MAX_ERROR_COUNT = 5;

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
            if (!errorCheck(command)) {
                System.out.println("[COMMAND THREAD] Processing command: " + command);

                // Execute system routines or get key for audio output
                String audioKey = CommandUtil.runCommand(command);
                // Forward key to audio output if necessary
                //don't forward broken commands
                if (!errorCheck(audioKey)) {
                    errorCount = 0; //a valid command was found put it in the q
                    audioQueue.offer(audioKey);
                    lastPlayed = audioKey;
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    private boolean errorCheck(String str){
        if(this.equals("error")||this.equals("")||this.equals(null)){
            errorCount++;
            return sendError();
        } else errorCount--;
        return false;
    }

    private boolean sendError(){
        if (errorCount >= MAX_ERROR_COUNT){//if nothing but garbage has been recorded for 5 consectuive commands, repeat
            audioQueue.offer("user input error");
            if(lastPlayed!=null){
                audioQueue.offer(lastPlayed); //repeat the previous level
            } else {
                audioQueue.offer("menu"); //no last level, return to the menu
            }
            return true;
        }
        return false;
    }
}

