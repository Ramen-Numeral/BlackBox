package game.tasks;

import game.gameObjects.GameLevel;
import game.gameObjects.WorldMap;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class CommandProcessorTask implements Runnable {

    private final ExecutorService audioExecutor;
    private final BlockingQueue<String> commandQueue;

    public CommandProcessorTask(ExecutorService executor, ExecutorService audioExecutor,
                                BlockingQueue<String> commandQueue) {
        this.audioExecutor = audioExecutor;
        this.commandQueue = commandQueue;
    }

    @Override
    public void run() {
        System.out.println("[PROCESSOR] Starting game...");

        GameLevel level;
        try {
            level = new CommandFetchTask("start menu").call(); // initial game state
            audioExecutor.submit(new AudioOutTask(level));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            while (true) {
                String command = commandQueue.take();

                if ("game over".equalsIgnoreCase(command) || "exit game".equalsIgnoreCase(command)) {
                    System.out.println("[PROCESSOR] Received exit command. Ending game loop.");
                    break;
                }
                level = new CommandFetchTask(command).call(); // update state
                audioExecutor.submit(new AudioOutTask(level));
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}
