package game.api;

import game.api.utilityJSON.AWSUtil;
import game.api.utilityJSON.OpenAIUtil;
import game.stateRoutines.envsetup.SetEnv;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class APICalls {

    //polly wrapper
    public static byte[] callPolly(String text) {
        try (InputStream in = AWSUtil.postPolly(text)) {
            return AWSUtil.pollyParseResponse(in);
        } catch (IOException e) {
            throw new RuntimeException("Polly TTS failed: " + e.getMessage(), e);
        }
    }

    //wrapper
    public static String callWhisper(String string){
        return OpenAIUtil.transcribeAudio(string);
    }
    //wrapper
    public static double[] callEmbeddings(String text){
        return OpenAIUtil.getEmbeddings(text);
    }


}
