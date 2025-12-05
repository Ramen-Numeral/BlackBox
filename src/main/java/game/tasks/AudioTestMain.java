/*package game.tasks;

import game.stateRoutines.envsetup.SetEnv;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class AudioTestMain {

    public static void main(String[] args) {
        // Load environment variables (must define USER_INPUT_FILE)
        SetEnv.load(".env");

        // Create a simple queue for commands (not used in this test)
        BlockingQueue<String> commandQueue = new ArrayBlockingQueue<>(10);

        // Start the MasterAudioTask in a separate thread
        MasterAudioTask audioTask = new MasterAudioTask(commandQueue);
        Thread listenerThread = new Thread(audioTask);
        listenerThread.start();

        System.out.println("Listening for speech. Speak into the microphone...");

        // Let it run for a while (e.g., 60 seconds), then stop
        try {
            Thread.sleep(60000); // 60 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Stop the thread gracefully
        listenerThread.interrupt();
        try {
            listenerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Test finished.");
    }
}
*/