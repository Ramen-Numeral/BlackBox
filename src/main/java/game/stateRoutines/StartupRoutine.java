package game.stateRoutines;

import game.gameUtil.helpers.WorldUtil;
import game.gameUtil.objs.WorldMap;
import game.stateRoutines.envsetup.SetEnv;

public class StartupRoutine {
    public static void startupRoutine(){
        SetEnv.load(".env");
        if(!WorldUtil.loadWorldMap()) {
            WorldMap.buildLevelMap();
        }
        if(WorldMap.isEmpty()){ //safety in case its not available
            WorldMap.buildLevelMap();
        }
    }

}


