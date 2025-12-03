package game.tasks;

import game.commandProcesses.CommandProcessor;
import game.gameObjects.GameLevel;

import java.util.concurrent.Callable;

/**
 * Callable task for executing a single game command.
 * Each instance is tied to a specific command string.
 */
public class CommandFetchTask implements Callable<GameLevel> {

    private final CommandProcessor processor;
    private final String command;

    /**
     * Create a task for a specific command.
     * @param command The command to process.
     */
    public CommandFetchTask(String command) {
        this.processor = new CommandProcessor();
        this.command = command;
    }

    /**
     * Executor will call this with no arguments.
     * Uses the command passed at construction.
     */
    @Override
    public GameLevel call() throws Exception {
        System.out.printf("[COMMAND THREAD] Processing command: '%s'%n", command);
        try {
            return processor.runCommand(command);
        } catch (Exception e) {
            System.err.println("[COMMAND THREAD] Error processing command: " + e.getMessage());
            return null;
        }
    }
}
