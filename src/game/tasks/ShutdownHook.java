package game.tasks;

import game.commandUtil.CommandUtil;
import game.gameUtil.helpers.StateUtil;
import game.gameUtil.helpers.WorldUtil;
import game.gameUtil.objs.GameState;
import game.gameUtil.objs.WorldMap;
import game.stateRoutines.envsetup.SetEnv;

import java.util.concurrent.ExecutorService;

public class ShutdownHook {

    public static void registerShutdownHook(ExecutorService executor,
                                            ExecutorService audioExecutor) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SAVER] Shutdown signal detected.");

            StateUtil.saveGame(WorldMap.getWorldMap(), CommandUtil.getLastLev().getCommand());
            WorldUtil.writeOutWorldMap(SetEnv.get("WORLD_SAVE_PATH"));

            System.out.println("[SAVER] Shutting down executors...");
            executor.shutdownNow();
            audioExecutor.shutdownNow();

        }, "Game-Save-Hook"));

        System.out.println("[SAVER] Shutdown hook registered.");
    }
}

