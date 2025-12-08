package game.gameUtil.objs;

import game.commandUtil.CommandUtil;
import game.stateRoutines.envsetup.SetEnv;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

//master object that holds command emb for all levels (mapped by command trigger)
//and the map to all levels
public final class WorldMap {
    private static final HashMap<String, GameLevel> worldMap = new HashMap<>();
    private static final HashMap<String, double[]> commandEmbeddings = new HashMap<>();

    public static void buildLevelMap() {
        String levelTextDir = SetEnv.get("LEVEL_DIRECTORY"); //dir holding all of the level templates
        File dir = new File(levelTextDir);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Directory not found or is not a directory: " + levelTextDir);
        }
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No .txt files found in directory: " + levelTextDir);
        }

        //clear stale info
        worldMap.clear();
        commandEmbeddings.clear();

        //loop through all files and turn them into a level / map the embedding
        Stream.of(files).forEach(txtFile -> {
            try {
                GameLevel newLev = new GameLevel(SetEnv.get("LEVEL_DIRECTORY") + txtFile.getName());
                worldMap.put(newLev.getCommand(), newLev);

                double[] embedding = newLev.getEmbedding();
                if (embedding != null) {
                    commandEmbeddings.put(newLev.getCommand(), Arrays.copyOf(embedding, embedding.length));
                }

            } catch (IOException | RuntimeException e) {
                System.err.println("Error processing file " + txtFile.getName() + ": " + e.getMessage());
            }
        });

        if (worldMap.isEmpty()) {
            throw new RuntimeException("No GameLevels were successfully created. Check error logs for details.");
        }
    }

    //copy volatile
    public static HashMap<String, GameLevel> getWorldMapModifiable() {
        return worldMap;
    }
    public static HashMap<String, double[]> getCommandEmbeddingsModifiable() {
        return commandEmbeddings;
    }

    // safe accessor
    public static Map<String, GameLevel> getWorldMap() {
        if (worldMap.isEmpty()) System.err.println("Warning: worldMap is empty.");
        return worldMap;
    }
    //utility stubs
    public static GameLevel getLevel(String command) { return worldMap.get(command); }
    public static boolean contains(String com){ return worldMap.containsKey(com); }
    public static Set<String> getLevelCommands() { return Collections.unmodifiableSet(worldMap.keySet()); }
    public static Collection<GameLevel> getAllLevels() { return Collections.unmodifiableCollection(worldMap.values()); }
    public static boolean isEmpty() { return worldMap.isEmpty(); }
    public static Map<String, double[]> getAllCommandEmbeddings() { return Collections.unmodifiableMap(commandEmbeddings); }
}
