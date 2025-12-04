package game;

import game.gameUtil.objs.WorldMap;
import game.stateRoutines.StartupRoutine;
import game.tasks.ShutdownHook;
import game.tasks.*;

import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {

        StartupRoutine.startupRoutine();

        ExecutorService executor = Executors.newFixedThreadPool(4);        // Listener + command processor
        ExecutorService audioExecutor = Executors.newSingleThreadExecutor(); // Serial audio playback
        BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();

        // Register the global shutdown save routine + executor cleanup
        ShutdownHook.registerShutdownHook(executor, audioExecutor);

        // Submit main tasks
        executor.submit(new ListenerTask(executor, commandQueue));
        executor.submit(new CommandExecTask(executor, audioExecutor, commandQueue));
    }
}
