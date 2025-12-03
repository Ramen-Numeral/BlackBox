package game.commandProcesses;

import game.gameObjects.GameLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//class that holds all commands available at any level and their resulting routine
public class CommandProcessor {

    private static final ArrayList<String> globalCommands = new ArrayList<String>();

    public CommandProcessor(){
    }

    public List<String> getGlobalCommands(){
        return Collections.unmodifiableList(globalCommands);
    }

    public boolean isGlobalCommand(String com){
        return globalCommands.contains(com);
    }

    public static void initGlobalCommands(){
        globalCommands.add("exit");
        globalCommands.add("error");
        globalCommands.add("save game");
        globalCommands.add("start game");
        globalCommands.add("load game");
        globalCommands.add("repeat choices");
        globalCommands.add("tutorial");
    }

    //will these be separate levels? they need to contain audio files.
    //maybe there is a game level initialized in the worldMap for each of these
    //but to run these commands, the process is called that specifies
    //the accompanying logic (save etc)
    //TODO "error, repeat choices special repeat choices for when could not process users commands then repeat the prompts
    //TODO all of these need to update nextLevel before returning;
    //TODO add a critical error game exit process
    //TODO all call runLevel after doing their bespoke processing stuff
    public GameLevel tutorialProcess(){}
    public GameLevel menuProcess(){}
    public GameLevel exitProcess(){}
    public GameLevel saveProcess(){}
    public GameLevel loadProcess(){}
    public GameLevel repeatProcess(){}
    public GameLevel errorProcess(){}
    public GameLevel startGameProcess(){}
    public GameLevel gameOverProcess(){}
    public GameLevel levelProcess(String string)
    public boolean canProcess(String com){
    //rapper bool
        return true;
    } // bool to help with switch so default error
//TODO implement an error for audio that interrupted the flow and doesnt ask a command gives a user a map of do you want the main menu, repeat your input, hear the choices again or exit the game. on game exit ask if use would like to save their progress
    public void runLevel(){} //process that runs the current level

    public GameLevel runCommand(String com){
        switch (com){
            case "exit" -> exitProcess();
            case "save game" -> saveProcess();
            case "error" -> errorProcess();
            case "load game" -> loadProcess();
            case "repeat choices"-> repeatProcess();
            case "tutorial" -> tutorialProcess();
            case "menu" -> menuProcess();
            case "start game"-> startGameProcess();
            case "game over" -> gameOverProcess();
            default -> {
                if(canProcess(com)){
                    levelProcess(com);
                    //do i need to return the level or handle running it inside level process?
                }else{ errorProcess(); }
            }
        }
    }





}
