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


    public GameLevel exitProcess(){
        systemExitRoutine();
        return levelProcess("exit"); //never returns hard jvm exit
    }
    public GameLevel saveProcess(){
        WorldUtil.writeOutWorldMap();
        StateUtil.saveGame(WorldMap.getWorldMap(), CommandUtil.getLastLev().getCommand());
        return levelProcess("menu");
    }
    public GameLevel loadProcess(){
        String com = StateUtil.loadGame(SetEnv.get("GAME_STATE_PATH"));
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
            case "resume game" -> {return levelProcess(prevLevel.getCommand());}
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
