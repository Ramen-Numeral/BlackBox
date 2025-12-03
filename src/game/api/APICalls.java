package game.api;

import game.startupRoutine.envsetup.SetEnv;
import java.io.IOException;
import java.io.InputStream;
import game.api.utilityJSON.BuildJSON;
import game.api.utilityJSON.ParseJSON;
import game.api.utilityJSON.PostJSON;

public class APICalls {

    /** Returns transcript of user audio using Whisper */
    public static String getAudioXscript(byte[] audioBytes) {
        try {
            // 1. Build JSON using the byte array
            String json = BuildJSON.buildJSON64(audioBytes);

            // 2. Post the JSON and get the raw response
            String rawResponse = PostJSON.postJSON(SetEnv.get("WHISPER_ENDPOINT"), json);

            // 3. Parse and return the transcription
            return ParseJSON.whisParseResponse(rawResponse);

        } catch (IOException e) {
            // Catch exceptions from PostJSON or SetEnv
            throw new RuntimeException("Error during API request for audio transcription.", e);
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Catch exceptions from BuildJSON.buildJSON64
            throw new RuntimeException("Error setting up transcription request: " + e.getMessage(), e);
        }
    }

    /** Calls embeddings endpoint and returns vector */
    public static double[] getEmbeddedVal(String xscript) throws IOException {
        String endpoint = SetEnv.get("OPENAI_EMBEDDINGS_ENDPOINT");
        String jsonBody = BuildJSON.buildEmbJSON(xscript);
        return ParseJSON.embParseResponse(PostJSON.postJSON(endpoint, jsonBody));
    }

    /** Converts a .txt file to speech using Amazon Polly and saves as wav */
    public static byte[] textToSpeech(String txt) {
        try {
            String endpoint = SetEnv.get("AWS_ENDPOINT");
            String jsonBody = BuildJSON.buildPollyJSON(txt);

            InputStream response = PostJSON.postJSONBinReturn(endpoint, jsonBody, SetEnv.get("POLLY_AUTH_HEADER"));

            byte[] audioBytes = ParseJSON.pollyParseResponse(response);
            return audioBytes;

        } catch (IOException e) {
            throw new RuntimeException("Error calling Polly TTS: " + e.getMessage(), e);
        }
    }
}
