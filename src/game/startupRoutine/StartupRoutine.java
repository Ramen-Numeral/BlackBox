package game.startupRoutine;

import game.gameObjects.WorldMap;
import game.startupRoutine.envsetup.SetEnv;

public class StartupRoutine {
    public static void startupRoutine(){
        SetEnv.load(".env");
        WorldMap.buildLevelMap(SetEnv.get("LEVEL_TXT_DIR"));
    }

}


