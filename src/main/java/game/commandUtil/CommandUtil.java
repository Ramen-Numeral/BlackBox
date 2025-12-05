package game.commandUtil;
import game.gameUtil.helpers.StateUtil;
import game.gameUtil.helpers.WorldUtil;
import game.gameUtil.objs.GameLevel;
import game.gameUtil.objs.GameState;
import game.gameUtil.objs.WorldMap;
import game.stateRoutines.envsetup.SetEnv;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//command routing
public class CommandUtil {

    private static GameLevel prevLevel = WorldMap.getLevel("start");
    private static final ArrayList<String> globalCommands = new ArrayList<String>();
    public static List<String> getGlobalCommands() {
        return Collections.unmodifiableList(globalCommands);
    }

    public static boolean isGlobalCommand(String com) {
        return globalCommands.contains(com);
    }
    public static GameLevel getLastLev() {
        return prevLevel;
    }

    //done during the creation of the world map
    public static void initGlobalCommands(){
        globalCommands.add("save");
        globalCommands.add("error");
        globalCommands.add("save game");
        globalCommands.add("start game");
        globalCommands.add("load game");
        globalCommands.add("repeat choices");
        globalCommands.add("tutorial");
        globalCommands.add("new game");
        globalCommands.add("game intro");
        globalCommands.add("exit");
    }


    public static String exitProcess(){
        systemExitRoutine();
        return levelProcess("exit"); //never returns hard jvm exit
    }
    public static String saveProcess(){
        WorldUtil.writeOutWorldMap();
        StateUtil.saveGame(WorldMap.getWorldMap(), CommandUtil.getLastLev().getCommand());
        return levelProcess("menu");
    }
    public static String loadProcess(){
        String com = StateUtil.loadGame(SetEnv.get("GAME_STATE_PATH"));
        return levelProcess(com);
    }
    public static String gameOverProcess(){
        gameOverRoutine();
        return levelProcess("exit"); //hard system exit
    }


    public static String levelProcess(String com){
        if(!WorldMap.contains(com)) return("error");
        if(!isGlobalCommand(com)) {
            WorldMap.getLevel(com).setPlayed(true); //dont make commands that are always available unavail
        }
        prevLevel = WorldMap.getLevel(com);
        return com;
    }

    public static String runCommand(String com){
        switch (com){
            case "exit" -> {return exitProcess();}
            case "save game" -> {return saveProcess();}
            case "load game" -> {return loadProcess();}
            case "game over" -> {return gameOverProcess();}
            case "resume game" -> {return prevLevel.getCommand();}
            default -> {return levelProcess(com);}
        }
    }


    public static void systemExitRoutine() {
        WorldUtil.writeOutWorldMap();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SAVER] System exit triggered.");
        }, "Game-Exit-Hook"));
        System.exit(0);
    }

    public static void gameOverRoutine() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SAVER] Game Over triggered.");
            WorldUtil.writeOutWorldMap();
            StateUtil.saveGame(WorldMap.getWorldMap(), "start");
        }, "Game-Save-Hook"));
        System.exit(0);
    }
}
