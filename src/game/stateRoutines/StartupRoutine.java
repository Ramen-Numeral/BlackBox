package game.stateRoutines;

import game.gameUtil.helpers.WorldUtil;
import game.gameUtil.objs.WorldMap;
import game.stateRoutines.envsetup.SetEnv;

public class StartupRoutine {
    public static void startupRoutine(){
        SetEnv.load(".env");
        if(!WorldUtil.loadWorldMap(SetEnv.get("WORLD_SAVE_PATH"))) {
            WorldMap.buildLevelMap(SetEnv.get("LEVEL_TXT_DIR"));
        }
        if(WorldMap.isEmpty()){ //safety in case its not available
            WorldMap.buildLevelMap(SetEnv.get("LEVEL_TXT_DIR"));
        }
    }

}


