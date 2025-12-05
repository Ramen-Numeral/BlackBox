package game.api.utilityJSON;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.audio.AudioModel;
import com.openai.models.audio.transcriptions.Transcription;
import com.openai.models.audio.transcriptions.TranscriptionCreateParams;
import com.openai.models.embeddings.CreateEmbeddingResponse;
import com.openai.models.embeddings.Embedding;
import com.openai.models.embeddings.EmbeddingCreateParams;
import game.stateRoutines.envsetup.SetEnv;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class OpenAIUtil {

    /**
     * Transcribe audio bytes using Whisper model
     */

    public static OpenAIClient clientUtil(){
        SetEnv.load(".env");
        String apiKey = SetEnv.get("OPENAI_API_KEY");

        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
        return client;
    }

    public static String transcribeAudio(String string) {
        OpenAIClient client = clientUtil();
        String outputFile = string;
        File audioFile = new File(string);

        try {
            TranscriptionCreateParams params = TranscriptionCreateParams.builder()
                    .file(audioFile.toPath())           // pass File object, not string
                    .model(AudioModel.WHISPER_1)
                    .build();

            Transcription transcription = client.audio()
                    .transcriptions()
                    .create(params)
                    .asTranscription();

            System.out.println("inside util class fn: Transcript: " + transcription.text());

            return transcription.text();
        } catch (Exception e) {
            throw new RuntimeException("Whisper transcription failed: " + e.getMessage(), e);
        }
    }


    /**
     * Get embeddings for text
     */
    public static double[] getEmbeddings(String text) {
        OpenAIClient client = clientUtil();

        try {
            EmbeddingCreateParams params = EmbeddingCreateParams.builder()
                    .model(SetEnv.getOrSet("EMBEDDINGS_MODEL", "text-embedding-3-small"))
                    .input(text)
                    .build();

            CreateEmbeddingResponse response = client.embeddings().create(params);

            List<Embedding> embeddingsList = response.data();
            if (embeddingsList.isEmpty()) {
                throw new RuntimeException("No embeddings returned from API");
            }

            List<Float> ls = embeddingsList.getFirst().embedding();

            double[] arr = new double[ls.size()];
            for (int i = 0; i < ls.size(); i++) {
                arr[i] = ls.get(i);
            }

            return arr;

        } catch (Exception e) {
            throw new RuntimeException("Embeddings request failed: " + e.getMessage(), e);
        }
    }


    /**
     * Cosine similarity helper
     */
    public static double cosineSimilarity(List<Double> v1, List<Double> v2) {
        if (v1.size() != v2.size()) throw new IllegalArgumentException("Vector sizes must match");
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            normA += v1.get(i) * v1.get(i);
            normB += v2.get(i) * v2.get(i);
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
