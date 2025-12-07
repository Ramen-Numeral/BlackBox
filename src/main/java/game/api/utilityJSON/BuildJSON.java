//deprecated by sdk


/*package game.api.utilityJSON;
import game.stateRoutines.envsetup.SetEnv;

import java.io.*;
import java.util.Base64;

public class BuildJSON {


    /**
     * Builds the JSON payload for the Whisper API by encoding raw audio bytes in Base64.
     *
     * @param audioBytes The raw audio data as a byte array.
     * @return The JSON string containing the model and the Base64-encoded audio file contents.
     */
  /*  public static String buildJSON64(byte[] audioBytes) {
        // Check API key exists
        String apiKey = System.getenv("OPENAI_API_KEY");
        String model = SetEnv.getOrSet("WHISPER_MODEL", "whisper-1");

        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("No API key found!");
        }

        if (audioBytes == null || audioBytes.length == 0) {
            throw new IllegalArgumentException("Input audio byte array cannot be null or empty.");
        }

        // Convert to base64
        // Note: The audio data must be a properly formatted file (e.g., WAV, MP3)
        // including the headers, not just raw PCM data.
        String base64Audio = Base64.getEncoder().encodeToString(audioBytes);

        // Build JSON string
        String json = "{"
                + "\"model\":\"" + model + "\","
                + "\"file\":\"" + base64Audio + "\""
                + "}";

        return json;
    }

    public static String buildEmbJSON(String transcript) {
        if (transcript == null) transcript = "";  // safety fallback
        String escaped = transcript.replace("\"", "\\\""); // escape quotes
        String model = SetEnv.getOrSet("EMBEDDINGS_MODEL", "text-embedding-3-small");

        return "{"
                + "\"model\":\"" + model + "\","
                + "\"input\":\"" + escaped + "\""
                + "}";
    }
    public static void main(String[] args) {
        // Load environment variables first
        SetEnv.load(".env");

        // ----------- Test buildJSON64 -----------
        try {
            System.out.println("Testing buildJSON64...");
            // Simple fake audio data (small byte array)
            byte[] audioBytes = "TestAudioData".getBytes();

            String json64 = BuildJSON.buildJSON64(audioBytes);
            System.out.println("JSON64 output:");
            System.out.println(json64);

            // Basic sanity checks
            if (!json64.contains("model") || !json64.contains("file")) {
                System.err.println("buildJSON64 failed: missing required fields.");
            } else {
                System.out.println("buildJSON64 test passed.");
            }

        } catch (Exception e) {
            System.err.println("buildJSON64 test failed: " + e.getMessage());
            e.printStackTrace();
        }

        // ----------- Test buildEmbJSON -----------
        try {
            System.out.println("\nTesting buildEmbJSON...");
            String transcript = "Hello \"world\"! Testing embedding JSON.";

            String embJSON = BuildJSON.buildEmbJSON(transcript);
            System.out.println("Embedding JSON output:");
            System.out.println(embJSON);

            // Check that quotes are escaped
            if (!embJSON.contains("\\\"world\\\"")) {
                System.err.println("buildEmbJSON failed: quotes not escaped properly.");
            } else {
                System.out.println("buildEmbJSON test passed.");
            }

        } catch (Exception e) {
            System.err.println("buildEmbJSON test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

}



*/
