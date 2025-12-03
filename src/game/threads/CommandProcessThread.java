package game.threads;

import java.util.concurrent.Callable;

/**
 * Worker thread for executing the core game command logic.
 * Implements Callable<CommandResult> because it processes user input
 * and returns the outcome (state transition, error, or exit) to the GameEngine.
 */
public class CommandProcessThread implements Callable<CommandResult> {

    private final String command;
    private final GameLevel currentLevel;
    private final GameState gameState;
    private final GameCommandProcessor processor;

    /**
     * Initializes the task with all necessary context for processing the command.
     * @param command The validated command string received from the AudioInputThread.
     * @param currentLevel The current level object.
     * @param gameState The current overall game state.
     */
    public CommandProcessingTask(String command, GameLevel currentLevel, GameState gameState) {
        this.command = command;
        this.currentLevel = currentLevel;
        this.gameState = gameState;
        // Instantiate the processor to access its logic
        this.processor = new GameCommandProcessor();
    }

    /**
     * The method that executes the command processing logic in a dedicated thread.
     * * @return A CommandResult object detailing the outcome of the command.
     * @throws Exception if an unexpected error occurs during processing (e.g., critical data inconsistency).
     */
    @Override
    public CommandResult call() throws Exception {
        System.out.printf("[COMMAND THREAD] Starting processing for command: '%s'\n", this.command);

        try {
            // Delegate the heavy lifting to the central processor class
            CommandResult result = processor.processCommand(
                    this.command,
                    this.currentLevel,
                    this.gameState
            );

            System.out.println("[COMMAND THREAD] Processing finished. Action: " + result.getAction());
            return result;

        } catch (Exception e) {
            System.err.println("[COMMAND THREAD] CRITICAL FAILURE during command processing: " + e.getMessage());
            // Re-throw to propagate to the GameEngine's Future.get()
            throw new Exception("Command logic failed.", e);
        }
    }
}