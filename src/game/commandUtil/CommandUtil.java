package game.commandProcesses;
import game.gameObjects.GameLevel;
import game.gameObjects.GameState;
import game.gameObjects.WorldMap;
import game.stateRoutines.envsetup.SetEnv;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//command routing
public class CommandProcessor {

    private static final String SAVE_FILE_PATH = "savegame.dat";
    private static GameLevel prevLevel = WorldMap.getLevel("start");
    private static final ArrayList<String> globalCommands = new ArrayList<String>();
    public List<String> getGlobalCommands() {
        return Collections.unmodifiableList(globalCommands);
    }

    public boolean isGlobalCommand(String com) {
        return globalCommands.contains(com);
    }

    public static GameLevel getLastLev() {
        return prevLevel;
    }

    public static void initGlobalCommands(){
        globalCommands.add("save and exit");
        globalCommands.add("error");
        globalCommands.add("save game");
        globalCommands.add("start game");
        globalCommands.add("load game");
        globalCommands.add("repeat choices");
        globalCommands.add("tutorial");
        globalCommands.add("new game");
    }

    //TODO
    public GameLevel exitProcess(){
        systemExitRoutine();
        return levelProcess("exit"); //never returns hard jvm exit
    }
    public GameLevel saveProcess(){
        GameState.saveGame(WorldMap.getWorldMap(), CommandProcessor.getLastLev().getCommand());
        return levelProcess("menu");
    }
    public GameLevel loadProcess(){
        String com = GameState.loadGame(SetEnv.get("SAVE_PATH"));
        return levelProcess(com);
    }
    public GameLevel gameOverProcess(){
        gameOverRoutine();
        return levelProcess("exit"); //hard system exit
    }


    public GameLevel levelProcess(String com){
        if(!WorldMap.contains(com)) return(WorldMap.getLevel("error"));
        GameLevel toPlay = WorldMap.getLevel(com);
        if(isGlobalCommand(com)) {
            WorldMap.getLevel(com).setPlayed(true); //dont make commands that are always available unavail
        }
        prevLevel = toPlay;
        return WorldMap.getLevel(com);
    }

    public GameLevel runCommand(String com){
        switch (com){
            case "exit" -> {return exitProcess();}
            case "save game" -> {return saveProcess();}
            case "load game" -> {return loadProcess();}
            case "game over" -> {return gameOverProcess();}
            default -> {return levelProcess(com);}
        }
    }

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



    public static void systemExitRoutine() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SAVER] System exit triggered.");
        }, "Game-Exit-Hook"));
        System.exit(0);
    }

    public static void gameOverRoutine() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SAVER] Game Over triggered.");
            saveGameState(new GameState());
        }, "Game-Save-Hook"));

        System.exit(0);
    }
}
