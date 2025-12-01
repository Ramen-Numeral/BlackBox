package game.api.utilityJSON;
import game.api.APICalls;
import game.envsetup.SetEnv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class BuildJSON {

    public static String buildJSON64(String audioPath) {
        // Check API key exists
        String apiKey = SetEnv.get("OPEN_AI_API_KEY");
        String model = SetEnv.getOrSet("WHISPER_MODEL", "whisper-1");

        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("No API key found!");
        }

        File audio = new File(audioPath);
        if (!audio.exists()) {
            throw new IllegalArgumentException("Audio file not found: " + audioPath);
        }

        try (FileInputStream fIn = new FileInputStream(audio)) {
            //convert to base64
            byte[] ab = new byte[(int) audio.length()];
            fIn.read(ab);
            String base64Audio = Base64.getEncoder().encodeToString(ab);

            // Build JSON string
            String json = "{"
                    + "\"model\":\"" + model + "\","
                    + "\"file\":\"" + base64Audio + "\""
                    + "}";

            return json;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read audio file for JSON creation", e);
        }
    }

    public static String buildEmbJSON(String audioPath){
        String postStr = APICalls.getAudioXscript(audioPath)
                .replace("\"", "\\\""); // escape quotes
        String model = SetEnv.getOrSet("EMBEDDINGS_MODEL", "text-embedding-3-small");

        String json = "{"
                + "\"model\":\"" + model + "\","
                + "\"input\":\"" + postStr + "\""
                + "}";

        return json;
    }
}