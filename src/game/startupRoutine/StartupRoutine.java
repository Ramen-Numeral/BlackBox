package game.startupRoutine;

import game.gameObjects.GameWorld;
import game.startupRoutine.envsetup.SetEnv;

public class StartupRoutine {
    public static void startupRoutine(){
        SetEnv.load(".env");
        GameWorld.buildLevelMap(SetEnv.get("LEVEL_TXT_DIR"));
    }

}


