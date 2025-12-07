/*package game.audioUtil.audioIn;


import game.stateRoutines.envsetup.SetEnv;

public class InputEventTest {
    public static void main(String[] args) {
        SetEnv.load(".env");
        System.out.println("Starting audio capture test... Speak into the mic.");

        try {
            // Capture user audio
            byte[] audioData = CaptureAudio.captureUserAudio();
            System.out.println("Captured " + audioData.length + " bytes of audio.");

            // Create an AudioInputEvent
            AudioInputEvent inputEvent = new AudioInputEvent();

            // Print the transcript and embedding
            System.out.println("Transcript: " + inputEvent.getTranscript());
            System.out.println("Event object: " + inputEvent);


        } catch (Exception e) {
            System.err.println("Error during audio capture or event creation:");
            e.printStackTrace();
        }
    }
}
*/