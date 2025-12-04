package game.gameObjects;

import game.audioUtil.audioOut.AudioOutput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GameUtil {

    public static void playPromptChoices(GameLevel level) {
        level.getAvailableCommands().stream()
                .map(WorldMap::getLevel)          // map command to GameLevel
                .filter(choice -> choice != null && !choice.isPlayed())
                .forEach(choice -> AudioOutput.playByteArray(choice.getCommandPromptAudio()));
    }

    public static void playNarrationAudio(GameLevel level){
        AudioOutput.playByteArray(level.getNarrationAudio());
    }

    public static HashMap<String, String> parseLvlTxt(String txtpath){
        String localPath = txtpath.trim().toLowerCase();
        HashMap<String, String> label2val = new HashMap<>();

        if(!localPath.endsWith(".txt")){
            localPath += ".txt";
        }

        File txt = new File(localPath);
        String line;

        try(BufferedReader buff = new BufferedReader(new FileReader(txt))){
            while((line = buff.readLine())!=null){
                String[] vals = line.split(":");

                if(vals.length==2) {
                    // Store the key/value pair
                    label2val.put(vals[0].trim().toLowerCase(),
                            vals[1].trim().toLowerCase());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to open or read txt file: " + localPath);
            throw new RuntimeException("Error during file parsing: " + localPath, e);
        }
        return label2val;
    }

    public static ArrayList<String> createAvailableCommand(HashMap<String, String> vals){
        String options = vals.get("available commands");
        ArrayList<String> coms = new ArrayList<>();
        if (options != null) {
            String[] commands = options.trim().split("\\$");
            coms.addAll(Arrays.asList(commands));
        }
        return coms;
    }

}
