package game.commandUtil;
import game.gameUtil.helpers.WorldUtil;
import game.gameUtil.objs.GameLevel;
import game.gameUtil.objs.WorldMap;
import java.util.ArrayList;


//command routing
public class CommandUtil {

    private static final ArrayList<String> globalCommands = new ArrayList<String>();

    public static void initGlobalCommands(){
        globalCommands.add("start a new game");
        globalCommands.add("go back");
    }

    public static String exitProcess(){
        systemExitRoutine();
        return levelProcess("exit"); //never returns hard jvm exit
    }


    //check for the command, return
    public static String levelProcess(String com){
        if(!WorldMap.contains(com)) return("error");
        System.out.println("[PROCESS UTIL] Processing command: " + com + " passed world map check");
        GameLevel lev = WorldMap.getLevel(com);
        System.out.println(lev.toString());

        return com;
    }

    public static String runCommand(String com){
       com = com.trim().toLowerCase();
        switch (com){
            case "exit" -> {return exitProcess();}
            //special cases for pipeline commands not available to the user
            case "command intro" -> {return "command intro";}
            case "user input error" -> {return "user input error";}
            default -> {return levelProcess(com);}
        }
    }


    public static void systemExitRoutine() {
        //save world
        WorldUtil.writeOutWorldMap();
        //exit system
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SAVER] System exit triggered.");
        }, "Game-Exit-Hook"));
        System.exit(0);
    }

}
