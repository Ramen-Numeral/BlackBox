package game.gameUtil.helpers;

import game.audioUtil.audioOut.AudioOutput;
import game.commandUtil.CommandUtil;
import game.gameUtil.objs.GameLevel;
import game.gameUtil.objs.WorldMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

//aux class to support the GameLevel object
public class LevelUtil {

    public static void playPromptChoices(GameLevel level) {
        level.getAvailableCommands().stream()
                .map(WorldMap::getLevel)          // map command to GameLevel
                .filter(choice -> choice != null && !choice.isPlayed())
                .forEach(choice -> AudioOutput.playByteArray(choice.getCommandPromptAudio()));
    }

    public static void playNarrationAudio(GameLevel level){
        AudioOutput.playByteArray(level.getNarrationAudio());
    }

    //read the template, add it to a map for commands, next steps, and narration text
    public static HashMap<String, String> parseLvlTxt(String txtpath){
        String localPath = txtpath.trim().toLowerCase();
        HashMap<String, String> label2val = new HashMap<>();

        if(!localPath.endsWith(".txt")){
            localPath += ".txt";
        }

        File txt = new File(System.getProperty("user.dir"), localPath);
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
    //loops through commands listed in the level template, adds them to be played back to the user as next steps
    public static ArrayList<String> createAvailableCommand(HashMap<String, String> vals){
        String options = vals.get("available commands");
        ArrayList<String> coms = new ArrayList<>();
        if (options != null) {
            String[] commands = options.trim().split("\\$");
            coms.addAll(Arrays.asList(commands));
            coms.add(coms.size()-1, "or");
        }
        return coms;
    }

}
