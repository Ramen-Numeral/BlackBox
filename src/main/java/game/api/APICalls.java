package game.api;

import game.api.utilityJSON.AWSUtil;
import game.api.utilityJSON.OpenAIUtil;
import game.stateRoutines.envsetup.SetEnv;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class APICalls {

    /**
     * Simple wrapper to call Polly and get audio bytes in one call.
     */
    public static byte[] callPolly(String text) {
        try (InputStream in = AWSUtil.postPolly(text)) {
            return AWSUtil.pollyParseResponse(in);
        } catch (IOException e) {
            throw new RuntimeException("Polly TTS failed: " + e.getMessage(), e);
        }
    }



    public static String callWhisper(String string){
        return OpenAIUtil.transcribeAudio(string);
    }

    public static double[] callEmbeddings(String text){
        return OpenAIUtil.getEmbeddings(text);
    }



    public static void main(String[] args) {
        // Load environment variables
        SetEnv.load(".env");

        boolean useLocalPolly = false; // Set to false to call AWS Polly live
        byte[] audioBytes;
        String transcript;
        double[] embeddings;

        try {
            // ------------------------
            // Polly audio
            // ------------------------
            if (useLocalPolly) {
                System.out.println("[TEST MODE] Loading local Polly audio from polly_test.mp3...");
                audioBytes = Files.readAllBytes(Paths.get("polly_response.mp3"));
            } else {
                System.out.println("Calling Polly to generate audio...");
                audioBytes = callPolly("Hello! This is a test of Polly, Whisper, and Embeddings integration.");
                System.out.println("Polly audio saved");
            }

            // ------------------------
            // Whisper transcription
            // ------------------------
           /* transcript = callWhisper(SetEnv.get("POLLY_OUTPUT_FILE");
            System.out.println("Transcript: " + transcript);

            // ------------------------
            // Embeddings
            // ------------------------
            embeddings = callEmbeddings(transcript);
            System.out.println("Embeddings vector length: " + embeddings.length);
            System.out.print("First 10 values: ");
            for (int i = 0; i < Math.min(10, embeddings.length); i++) {
                System.out.print(embeddings[i] + " ");
            }
            System.out.println(); */

        } catch (Exception e) {
            System.err.println("APICalls failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nAPICalls test complete.");
    }

}
