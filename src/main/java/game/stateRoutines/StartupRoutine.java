package game.stateRoutines;

import game.gameUtil.helpers.WorldUtil;
import game.gameUtil.objs.WorldMap;
import game.stateRoutines.envsetup.SetEnv;

import java.io.File;

public class StartupRoutine {
    public static void startupRoutine(){
        SetEnv.load(".env");


        WorldMap.buildLevelMap();


        if(WorldMap.isEmpty()){ // only rebuild if absolutely necessary
            System.out.println("[WORLD-LOADER] WorldMap still empty, ERROR.");
            }
        }
    }

