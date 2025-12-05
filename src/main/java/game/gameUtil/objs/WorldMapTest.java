package game.gameUtil.objs;

import game.stateRoutines.envsetup.SetEnv;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class WorldMapTest {

    public static void main(String[] args) {
        SetEnv.load(".env"); // load any environment variables you need for Polly/Embeddings
        System.out.println("=== WorldMap Test ===");

        String levelDir = "level_texts"; // directory containing .txt level files
        File dir = new File(levelDir);
        if (!dir.exists()) {
            System.err.println("Level directory not found: " + levelDir);
            return;
        }

        try {
            WorldMap.buildLevelMap();

            System.out.println("\n--- WorldMap Loaded Successfully ---");
            System.out.println("Total levels: " + WorldMap.getAllLevels().size());
            System.out.println("Level commands: " + WorldMap.getLevelCommands());

            System.out.println("\n--- Detailed Level Info ---");
            for (Map.Entry<String, GameLevel> entry : WorldMap.getWorldMap().entrySet()) {
                GameLevel level = entry.getValue();
                System.out.println("Command: " + level.getCommand());
                System.out.println("Text Path: " + level.getTxtPath());
                System.out.println("Available Commands: " + level.getAvailableCommands());
                System.out.println("Embedding length: " + (level.getEmbedding() != null ? level.getEmbedding().length : 0));
                System.out.println("Narration audio bytes: " + (level.getNarrationAudio() != null ? level.getNarrationAudio().length : 0));
                System.out.println("Command prompt audio bytes: " + (level.getCommandPromptAudio() != null ? level.getCommandPromptAudio().length : 0));
                System.out.println("----------------------------");
            }

            System.out.println("\n--- All Command Embeddings ---");
            WorldMap.getAllCommandEmbeddings().forEach((cmd, emb) -> {
                System.out.println("Command: " + cmd + ", Embedding length: " + (emb != null ? emb.length : 0));
            });

        } catch (IllegalArgumentException e) {
            System.err.println("Directory error: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error building WorldMap: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== Test Complete ===");
    }
}
