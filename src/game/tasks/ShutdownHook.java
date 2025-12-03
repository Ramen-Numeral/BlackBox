package game.stateRoutines;

import game.commandProcesses.CommandProcessor;
import game.gameObjects.GameState;
import game.gameObjects.WorldMap;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;

public class StateUtil {

    private static final String SAVE_FILE_PATH = "savegame.dat";

    /** ----------------------------
     *  INTERNAL SAVE LOGIC
     *  ---------------------------- */
    private static void saveGameState(GameState state) {
        System.out.println("[SAVER] Saving game state...");

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(SAVE_FILE_PATH))) {
            oos.writeObject(state);
            System.out.println("[SAVER] Game saved to: " + SAVE_FILE_PATH);
        } catch (Exception e) {
            System.err.println("[SAVER] ERROR: Failed to save game!");
            e.printStackTrace();
        }
    }


    private static GameState buildGameOverResetSave() {
        // New reset GameState (defaults to "start")
        return new GameState();
    }

    /** ----------------------------
     *  REGISTER JVM SHUTDOWN HOOK
     *  ---------------------------- */
    public static void registerShutdownHook(ExecutorService executor,
                                            ExecutorService audioExecutor) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SAVER] Shutdown signal detected.");

            GameState.saveGame(WorldMap.getWorldMap(), CommandProcessor.getLastLev().getCommand());

            System.out.println("[SAVER] Shutting down executors...");
            executor.shutdownNow();
            audioExecutor.shutdownNow();

        }, "Game-Save-Hook"));

        System.out.println("[SAVER] Shutdown hook registered.");
    }

    /** ----------------------------
     *  RUNTIME EXIT WITH SAVE
     *  ---------------------------- */
    public static void systemExitRoutine() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SAVER] System exit triggered.");
        }, "Game-Exit-Hook"));
        System.exit(0);
    }

    /** ----------------------------
     *  GAME OVER: RESET WORLD + EXIT
     *  ---------------------------- */
    public static void gameOverRoutine() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SAVER] Game Over triggered.");
            saveGameState(buildGameOverResetSave());
        }, "Game-Save-Hook"));

        System.exit(0);
    }
}
