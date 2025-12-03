package game.audioProcesses.audioIn;

import game.api.APICalls;
import game.audioProcesses.VectorComparison;
import game.gameObjects.GameWorld;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

//TODO listeneer to catch when user starts speaking

public class AudioInputEvent {
    private final String transcript;
    private final double[] embedding;
    private final byte[] input;
    private static final double MIN_MATCH_THRESHOLD = 0.75;


    public AudioInputEvent(byte[] input) {

        this.input = input;
        try {
            // Get transcript from Whisper
            this.transcript = APICalls.getAudioXscript(this.input);
            // Get embedding vector for the transcript
            this.embedding = APICalls.getEmbeddedVal(transcript);
        } catch (IOException e) {
            throw new RuntimeException("Error processing input audio: " + e.getMessage(), e);
        }
    }

    //TODO helper wrapper to capture and match commnad
    /*private String captureAndMatchUserCommand(){
    // 1. Capture Audio (Blocking operation using the method you defined)
    byte[] rawAudio = CaptureAudio.captureUserAudio();

    // 2. Process Audio Input (API calls for Xscript/Embedding)
    AudioInputEvent inputEvent = new AudioInputEvent(rawAudio);

    // 3. Match the command (uses the matchCommand() method you just wrote)
    String matchedCommand = inputEvent.matchCommand();

    return matchedCommand;
}*/
    /** Returns the transcript of this input */
    public String getTranscript() {
        return transcript;
    }

    /** Returns the embedding vector */
    public double[] getEmbedding() {
        return embedding;
    }


    public double getEmbeddingNorm() {
        if (embedding == null) return 0.0;
        double sum = 0.0;
        for (double v : embedding) {
            sum += v * v;
        }
        return Math.sqrt(sum);
    }

    public String toString() {
        return "InputEvent{" +
                ", transcript='" + (transcript != null ? transcript : "not transcribed") + '\'' +
                ", embedding=" + (embedding != null ? Arrays.toString(embedding) : "not embedded") +
                '}';
    }

    public String matchCommand(){
        if (embedding == null || embedding.length == 0) {
            System.err.println("Error: User input embedding is null or empty. Cannot match command.");
            return null;
        }

        // 1. Get all pre-calculated command embeddings from the GameWorld
        Map<String, double[]> commandEmbeddings = GameWorld.getAllCommandEmbeddings();

        if (commandEmbeddings.isEmpty()) {
            System.err.println("Error: GameWorld command embeddings map is empty. Cannot match.");
            return null;
        }

        String bestMatchCommand = null;
        double maxSimilarity = Double.MIN_VALUE;

        // 2. Iterate and compare
        for (Map.Entry<String, double[]> entry : commandEmbeddings.entrySet()) {
            String command = entry.getKey();
            double[] commandVector = entry.getValue();

            // Calculate cosine similarity
            double similarity = VectorComparison.cosineSimilarity(this.embedding, commandVector);

            // 3. Track the highest similarity
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                bestMatchCommand = command;
            }
        }

        // 4. Return the result if it meets the confidence threshold
        if (maxSimilarity >= MIN_MATCH_THRESHOLD) {
            System.out.printf("[MATCH] Found match: '%s' with confidence %.4f\n", bestMatchCommand, maxSimilarity);
            return bestMatchCommand;
        } else {
            System.out.printf("[MATCH] No confident match found. Max similarity was %.4f (Threshold: %.4f). Returning 'repeat options'.\n", maxSimilarity, MIN_MATCH_THRESHOLD);
            return "repeat options";
        }
    }
}
