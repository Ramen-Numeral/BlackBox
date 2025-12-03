/*package game.StartupRoutine;

import game.PlayEvents.GameState;
import game.PlayEvents.GameLevel;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public class ShutdownRoutine {

    public static void registerShutdownHook(final GameState gameState, final Map<String, GameLevel> worldMap) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SAVER] Detected shutdown signal. Starting save routine...");

            // 1. Create the low-overhead save file object
            GameState saveFile = GameState.saveGame(
                    worldMap,
                    gameState.getCurrentLevelCommand()
            );

            // 2. Serialize and write the save object to the persistent volume
            try (ObjectOutputStream oos = new Object ObjectOutputStream(
                    new FileOutputStream(SAVE_FILE_PATH))) {

                oos.writeObject(saveFile);
                System.out.println("[SAVER] Game state successfully saved to: " + SAVE_FILE_PATH);

            } catch (Exception e) {
                // IMPORTANT: Shutdown hooks should handle all exceptions internally.
                System.err.println("[SAVER] FATAL: Failed to save game state!");
                e.printStackTrace();
            }
        }, "Game-Save-Hook"));

        System.out.println("[SAVER] Shutdown hook registered.");
    }
}
*/