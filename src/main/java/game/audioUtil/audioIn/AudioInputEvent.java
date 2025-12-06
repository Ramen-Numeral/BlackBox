package game.audioUtil.audioIn;
import game.api.APICalls;
import game.audioUtil.VectorComparison;
import game.gameUtil.objs.WorldMap;
import game.stateRoutines.envsetup.SetEnv;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;


public class AudioInputEvent {
    private final String transcript;
    private final double[] embedding;
    private final byte[] input;
    private static final double MIN_MATCH_THRESHOLD = 0.75;

    //TODO assess if needed deprecated version, or add string arg,, never played back waste of mem?
    public AudioInputEvent(byte[] input) {
        this.input = input;
        this.transcript = APICalls.callWhisper(SetEnv.get("USER_INPUT_FILE"));
        String normal = transcript.toLowerCase().replaceAll("[^a-z ]", "");

        this.embedding = APICalls.callEmbeddings(normal);
    }
    public AudioInputEvent() {
        this.input = null;
        this.transcript = APICalls.callWhisper(SetEnv.get("USER_INPUT_FILE"));
        String normal = transcript.toLowerCase().replaceAll("[^a-z ]", "");
        this.embedding = APICalls.callEmbeddings(normal);
    }

    public String getTranscript() { return transcript; }


    @Override
    public String toString() {
        return "InputEvent{" +
                "transcript='" + (transcript != null ? transcript : "not transcribed") + '\'' +
                ", embedding=" + (embedding != null ? Arrays.toString(embedding) : "not embedded") +
                '}';
    }

    /**
     * Returns the best matched command based on cosine similarity using streams.
     * Falls back to "repeat options" if no match meets the threshold.
     */
    public String matchCommand() {
        if (embedding == null || embedding.length == 0) {
            System.err.println("Error: User input embedding is null or empty. Cannot match command.");
            return null;
        }

        Map<String, double[]> commandEmbeddings = WorldMap.getAllCommandEmbeddings();

        if (commandEmbeddings.isEmpty()) {
            System.err.println("Error: GameWorld command embeddings map is empty. Cannot match.");
            return null;
        }

        return commandEmbeddings.entrySet().stream()
                .max((a, b) -> Double.compare(
                        VectorComparison.cosineSimilarity(this.embedding, a.getValue()),
                        VectorComparison.cosineSimilarity(this.embedding, b.getValue())
                ))
                .filter(entry -> VectorComparison.cosineSimilarity(this.embedding, entry.getValue()) >= MIN_MATCH_THRESHOLD)
                .map(Map.Entry::getKey)
                .orElseGet(() -> {
                    System.out.println("[MATCH] No confident match found. Returning 'error'.");
                    return "error";
                });

        //TODO add a fallback in match command to send the keyset to chat gpt and match a likely match if emb returns null
    }
}
