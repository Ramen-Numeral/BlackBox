package game.api;

import game.envsetup.SetEnv;
import java.io.IOException;

import game.api.utilityJSON.BuildJSON;
import game.api.utilityJSON.ParseJSON;
import game.api.utilityJSON.PostJSON;

public class APICalls {

    //accepts user audio input path, returns xscript of input
    public static String getAudioXscript(String audioFilePath) {
        try {
            String json = BuildJSON.buildJSON64(audioFilePath);
            String rawResponse = PostJSON.postJSON(SetEnv.get("WHISPER_ENDPOINT"), json);
            return ParseJSON.whisParseResponse(rawResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /** Call the embeddings endpoint and return the vector */
    public static double[] getEmbeddedVal(String audioPath) throws IOException {
        String endpoint = SetEnv.get("OPENAI_EMBEDDINGS_ENDPOINT");
        String jsonBody = BuildJSON.buildEmbJSON(audioPath);
        String response = PostJSON.postJSON(endpoint, jsonBody);
        return ParseJSON.embParseResponse(response);
    }

}
