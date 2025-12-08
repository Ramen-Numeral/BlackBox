package game.stateRoutines;

import game.commandUtil.CommandUtil;
import game.gameUtil.helpers.WorldUtil;
import game.gameUtil.objs.WorldMap;
import game.stateRoutines.envsetup.SetEnv;

import static game.gameUtil.helpers.WorldUtil.loadWorldMap;

//loads the levels if there are any in the .dat file
//if not, builds the map / does the calls for audio embeddings etc.
public class StartupRoutine {
    public static void startupRoutine(){
        SetEnv.load(); //get the environmental vars from .env and .env.secrets
        CommandUtil.initGlobalCommands();

        if(!loadWorldMap()) {
            WorldMap.buildLevelMap();
        }

        WorldUtil.writeOutWorldMap();

        if(WorldMap.isEmpty()){ // extra catch in case build/load falsely succeeds
            System.out.println("[WORLD-LOADER] WorldMap still empty, ERROR.");
            }
        }

}

