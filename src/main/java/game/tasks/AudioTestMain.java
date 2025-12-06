package game.tasks;

import game.gameUtil.objs.WorldMap;
import game.stateRoutines.envsetup.SetEnv;

import java.util.concurrent.*;

public class AudioTestMain {
    public static void main(String[] args) throws Exception {
        SetEnv.load(".env");
    //    WorldMap.buildLevelMap();
        // Queue for receiving futures from listener
        BlockingQueue<CompletableFuture<String>> commandQueue = new LinkedBlockingQueue<>();

        // Create and start listener thread
        ListenerTask listener = new ListenerTask(commandQueue);
        Thread listenerThread = new Thread(listener);
        listenerThread.start();

        System.out.println("Listener started. Speak something...");

        // Take one future from the queue (blocking)
        CompletableFuture<String> future = commandQueue.take();

        System.out.println("Recording in progress...");

        // Wait for recorder to finish
        String result = future.get(); // blocks until recording + command matching completes

        System.out.println("Recognized command: " + result);

        // Clean up: stop listener
        listenerThread.interrupt();
        listenerThread.join();
        System.out.println("Listener stopped.");
    }
}
