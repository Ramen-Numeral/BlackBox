package game.gameObjects;
/*
public class CommandUtilities {



    //TODO utility helpers for the processthread to use / implement logic
    public class GameCommandProcessor {

        // Helper data structure to return complex results from command processing
        private static class CommandResult {
            enum Action { EXIT, REPEAT, TRANSITION, ERROR }
            private Action action;
            private GameLevel nextLevel; // Null unless action is TRANSITION
            // (Constructor, Getters/Setters)
        }

        // Handles logic for matching the initial command to an initial level
        public static GameLevel determineInitialLevel(String initialCommand, Map<String, GameLevel> worldMap) {
            // Logic to select the correct starting level key based on initialCommand
            // E.g., if "NEW_GAME", return worldMap.get("start_level_key");
            // E.g., if "TUTORIAL", return worldMap.get("tutorial_level_key");
            // ...
        }

        // Handles the core logic after a command is matched in the game loop
        public static CommandResult processCommand(String command, GameLevel currentLevel, GameState gameState) {
            switch (command):
            case "EXIT":
            return new CommandResult(Action.EXIT);

            case "REPEAT_OPTIONS":
            return new CommandResult(Action.REPEAT);

            case "GO_NORTH":
            case "TAKE_KEY":
            // 1. Check if the command is valid in the current level's context
            if (currentLevel.isValidCommand(command)) {
                // 2. Get the key for the next level (or the same level if state changed)
                GameLevel nextLevel = currentLevel.getNextLevel(command);
                return new CommandResult(Action.TRANSITION, nextLevel);
            } else {
                // Command is valid globally but not here (e.g., "GO NORTH" where no path exists)
                return new CommandResult(Action.ERROR);
            }

            default:
            // Command not recognized or not handled
            return new CommandResult(Action.ERROR);
        }
    }

    // Handles the clean shutdown and saving
    public static void handleExit(GameState gameState) {
        // 1. Save state to Docker Volume
        GameState.saveGame(gameState, GameWorld.getWorldMap());
        // 2. Clean up resources (if necessary)
        System.out.println("Game saved and shutting down.");
    }
}
*/

