package game.gameUtil.objs;

import game.stateRoutines.envsetup.SetEnv;

import java.io.IOException;

public class GameLevelTest {

    public static void main(String[] args) {
        SetEnv.load(".env");
        System.out.println("=== GameLevel Test ===");
/*
        if (args.length == 0) {
            System.err.println("Usage: GameLevelTest <path-to-level.txt>");
            return;
        }*/

        String txtPath = "level_texts/level_template";
        System.out.println("Loading level file: " + txtPath);


        try {
            GameLevel level = new GameLevel(txtPath);

            System.out.println("\n--- GameLevel Constructed Successfully ---");
            System.out.println(level); // uses your toString()

            System.out.println("\n--- Detailed Field Check ---");
            System.out.println("Command: " + level.getCommand());
            System.out.println("Available Commands: " + level.getAvailableCommands());
            System.out.println("Embedding length: " + (level.getEmbedding() != null ? level.getEmbedding().length : 0));
            System.out.println("Prompt audio bytes: " + (level.getCommandPromptAudio() != null ? level.getCommandPromptAudio().length : 0));
            System.out.println("Narration audio bytes: " + (level.getNarrationAudio() != null ? level.getNarrationAudio().length : 0));

        } catch (IOException e) {
            System.err.println("IO ERROR loading level:");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected ERROR during GameLevel creation:");
            e.printStackTrace();
        }

        System.out.println("\n=== Test Complete ===");
    }
}
