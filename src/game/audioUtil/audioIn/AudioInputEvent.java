package game.audioProcesses.audioIn;
import game.api.APICalls;
import game.audioProcesses.VectorComparison;
import game.gameObjects.WorldMap;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;


public class AudioInputEvent {
    private final String transcript;
    private final double[] embedding;
    private final byte[] input;
    private static final double MIN_MATCH_THRESHOLD = 0.75;

    public AudioInputEvent(byte[] input) {
        this.input = input;
        try {
            this.transcript = APICalls.getAudioXscript(this.input);
            this.embedding = APICalls.getEmbeddedVal(transcript);
        } catch (IOException e) {
            throw new RuntimeException("Error processing input audio: " + e.getMessage(), e);
        }
    }

    public String getTranscript() { return transcript; }
    public double[] getEmbedding() { return embedding; }

    public double getEmbeddingNorm() {
        if (embedding == null) return 0.0;
        return Arrays.stream(embedding)
                .map(v -> v * v)
                .sum();
    }

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
                    System.out.println("[MATCH] No confident match found. Returning 'repeat options'.");
                    return "repeat options";
                });
    }
}
