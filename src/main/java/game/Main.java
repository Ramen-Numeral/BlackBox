package game;

import game.audioUtil.audioOut.AudioOutput;
import game.commandUtil.CommandUtil;
import game.gameUtil.objs.WorldMap;
import game.stateRoutines.StartupRoutine;
import game.tasks.AudioOutTask;
import game.tasks.ListenerTask;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    public static void main(String[] args) throws Exception {
        StartupRoutine.startupRoutine();
        System.out.println("Starting audio pipeline...");

      //  System.out.println(WorldMap.getWorldMap());
        // Queue for passing audio commands from recorder -> command processor
        BlockingQueue<CompletableFuture<String>> commandQueue = new ArrayBlockingQueue<>(10);

        // Queue for passing audio keys to audio output
        BlockingQueue<String> audioQueue = new ArrayBlockingQueue<>(10);

        // Flag to interrupt audio playback if a new command comes in
        AtomicBoolean stopFlag = new AtomicBoolean(false);

        //initial startup of game
        AudioOutput.playByteArray(WorldMap.getLevel("start game").getNarrationAudio());
        AudioOutput.playByteArray(WorldMap.getLevel("start game").getCommandPromptAudio());

        // Start Listener thread
        ListenerTask listener = new ListenerTask(commandQueue);
        Thread listenerThread = new Thread(listener, "ListenerThread");
        listenerThread.start();

        // Start Command processing thread
        Thread commandThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    CompletableFuture<String> future = commandQueue.take(); // wait for recorder
                    String command = future.get(); // wait until recorder finishes

                    // check for system routines
                    command = CommandUtil.runCommand(command);

                    // push string to audio output queue
                    stopFlag.set(true); // interrupt current playback if any
                    audioQueue.offer(command);
                    stopFlag.set(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "CommandThread");
        commandThread.start();
        // Start Audio Output thread
        Thread audioThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String audioKey = audioQueue.take(); // wait for new audio command
                    AudioOutTask audioTask = new AudioOutTask(stopFlag, audioQueue);
                    audioTask.run(); // blocking playback
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "AudioThread");
        audioThread.start();
    }


}

/*
public class Main {

    public static void main(String[] args) {

        StartupRoutine.startupRoutine();

        ExecutorService executor = Executors.newFixedThreadPool(4);        // Listener + command processor
        ExecutorService audioExecutor = Executors.newSingleThreadExecutor(); // Serial audio playback
        BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();

        // Register the global shutdown save routine + executor cleanup
        ShutdownHook.registerShutdownHook(executor, audioExecutor);



    }
}*/
