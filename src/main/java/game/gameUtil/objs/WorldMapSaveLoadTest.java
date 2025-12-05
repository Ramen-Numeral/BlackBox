package game.gameUtil.objs;

import game.gameUtil.helpers.WorldUtil;
import game.stateRoutines.envsetup.SetEnv;

import java.io.File;

public class WorldMapSaveLoadTest {

    public static void main(String[] args) {
        SetEnv.load(".env");
        System.out.println("=== WorldMap Save/Load Test ===");

        String saveFile = "worldmap_test_save.dat";

        // 1. Build the WorldMap
        try {
            System.out.println("starting world map .");
            WorldMap.buildLevelMap();
            System.out.println("WorldMap built successfully.");
        } catch (Exception e) {
            System.err.println("Failed to build WorldMap:");
            e.printStackTrace();
            return;
        }

        // 2. Set first level to played
        GameLevel firstLevel = WorldMap.getAllLevels().stream().findFirst().orElse(null);
        if (firstLevel == null) {
            System.err.println("No levels found in WorldMap.");
            return;
        }
        firstLevel.setPlayed(true);
        System.out.println("Set level '" + firstLevel.getCommand() + "' played state to true.");

        // 3. Save WorldMap
        boolean saved = WorldUtil.writeOutWorldMap();
        if (!saved) {
            System.err.println("Failed to save WorldMap.");
            return;
        }

        // 4. Clear WorldMap
        WorldMap.getWorldMap().clear();
        System.out.println("Cleared WorldMap.");

        // 5. Load WorldMap
        boolean loaded = WorldUtil.loadWorldMap();
        if (!loaded) {
            System.err.println("Failed to load WorldMap.");
            return;
        }

        // 6. Check played state
        GameLevel loadedLevel = WorldMap.getLevel(firstLevel.getCommand());
        if (loadedLevel != null) {
            System.out.println("Loaded level '" + loadedLevel.getCommand() + "' played state: " + loadedLevel.isPlayed());
        } else {
            System.err.println("Failed to find level after loading.");
        }


        System.out.println("\n=== Test Complete ===");
    }
}
