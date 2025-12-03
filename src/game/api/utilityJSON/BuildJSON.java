package game.api.utilityJSON;
import game.startupRoutine.envsetup.SetEnv;

import java.io.*;
import java.util.Base64;

public class BuildJSON {


    /**
     * Builds the JSON payload for the Whisper API by encoding raw audio bytes in Base64.
     *
     * @param audioBytes The raw audio data as a byte array.
     * @return The JSON string containing the model and the Base64-encoded audio file contents.
     */
    public static String buildJSON64(byte[] audioBytes) {
        // Check API key exists
        String apiKey = SetEnv.get("OPEN_AI_API_KEY");
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


    /**
         * Builds a JSON body for Polly TTS from a text file.
         * Dynamically uses POLLY_VOICE from environment variables.
         */
        public static String buildPollyJSON(String text) throws IOException {
            // Get voice from environment
            String voice = System.getenv("POLLY_VOICE");
            if (voice == null || voice.isEmpty()) {
                voice = "Joanna"; // default if not set
            }

            // Build JSON
            return "{"
                    + "\"Text\":\"" + text + "\","
                    + "\"OutputFormat\":\"wav\","
                    + "\"VoiceId\":\"" + voice + "\""
                    + "}";
        }
    }

