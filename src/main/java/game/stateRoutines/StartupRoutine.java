package game.stateRoutines;

import game.commandUtil.CommandUtil;
import game.gameUtil.helpers.WorldUtil;
import game.gameUtil.objs.WorldMap;
import game.stateRoutines.envsetup.SetEnv;

import java.io.File;

import static game.gameUtil.helpers.WorldUtil.loadWorldMap;

public class StartupRoutine {
    public static void startupRoutine(){
        SetEnv.load();
        CommandUtil.initGlobalCommands();

        if(!loadWorldMap()) {
            WorldMap.buildLevelMap();
        }

        WorldUtil.writeOutWorldMap();
        //TODO add a clear on all of the setPlayed();
        if(WorldMap.isEmpty()){ // only rebuild if absolutely necessary
            System.out.println("[WORLD-LOADER] WorldMap still empty, ERROR.");
            }
        }

}

