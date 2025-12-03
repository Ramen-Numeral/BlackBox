package game.GameEngine;

public class gamePlay {
    public static void playGame() {
       // StartupRoutine.startupRoutine(); // remove me after it has been run once.

        //start audio capture thread.

        //game thread runnable(startup) //runnable will take a level as an argument

        //ask user for a high level command (tutorial, continue last game, new game, exit, repeat choices)
        //if they say load game and there is no load file, default to a new game

        //capture audio (inside audio thread runnable,
        // have it generate a audio input event from that)
        //match spoken command to a global command

        //call gamethread.runable(level);
        //audio output (taking in

        //audio cature

        //gamethread.runnable


        //game thread.runnable handles all of the command matching, level pulls etc.
        //audio input thread handles the whisper xscription/capturing audio etc
        //output thread plays audio back to the user based on the input command

    }

}
/*public class GameEngine {

    // Member Variables
    private GameState gameState;
    private AudioInputThread inputThread;
    private AudioOutput audioOutput;

    // 1. Constructor: Initializes core components
    public GameEngine() {
        // Assume GameWorld is already loaded via static block in GamePlay or GameWorld class
        this.gameState = new GameState();
        this.audioOutput = new AudioOutput();
        this.inputThread = new AudioInputThread();
    }

    // 2. Main Game Loop Method
    public static void playGame() {
        GameEngine engine = new GameEngine();
        engine.runGameLoop();
    }

    private void runGameLoop() {
        // --- Phase 1: Game Initialization / Load State ---

        // Ask user for a high-level command (Tutorial, Continue, New Game, Exit)
        this.audioOutput.playAudio(GameWorld.getAudio("initial_prompt"));

        // Start the input thread and block until a command is returned
        String initialCommand = this.inputThread.captureAndMatchCommand();

        // Load Game Logic
        if (initialCommand matches "LOAD_GAME"):
            boolean loaded = this.gameState.loadFromDisk();
            if (!loaded):
                initialCommand = "NEW_GAME";
                this.audioOutput.playAudio(GameWorld.getAudio("no_save_file_prompt"));

        // Set current level
        GameLevel currentLevel = GameCommandProcessor.determineInitialLevel(initialCommand, GameWorld.getWorldMap());
        this.gameState.setCurrentLevel(currentLevel);


        // --- Phase 2: Core Game Loop ---
        while (true) {

            // Check for game termination conditions
            if (this.gameState.isGameOver()) break;

            // Describe situation and prompt user
            this.audioOutput.playLevelDescription(currentLevel);
            this.audioOutput.playAudio(GameWorld.getAudio("action_prompt"));

            // Get user command (Blocking call via dedicated thread)
            String userCommand = this.inputThread.captureAndMatchCommand();

            // Process the command and get the next state
            CommandResult result = GameCommandProcessor.processCommand(
                userCommand,
                currentLevel,
                this.gameState
            );

            // --- Update State based on Result ---
            switch (result.getAction()):
                case EXIT:
                    GameCommandProcessor.handleExit(this.gameState);
                    return;
                case REPEAT:
                    // Loop continues (repeats level description)
                    break;
                case TRANSITION:
                    currentLevel = result.getNextLevel();
                    this.gameState.setCurrentLevel(currentLevel);
                    this.gameState.markPlayed(currentLevel.getCommand());
                    // Play confirmation audio (optional)
                    this.audioOutput.playAudio(currentLevel.getEntryAudio());
                    break;
                case ERROR:
                    this.audioOutput.playAudio(GameWorld.getAudio("error_audio_key"));
                    break;
        }
    }
}*/