package game.PlayEvents;

import game.api.APICalls;
import java.io.IOException;
import java.util.Arrays;

//TODO listeneer to catch when user starts speaking

public class InputEvent {
    private final String transcript;
    private final double[] embedding;
    private final byte[] input;


    public InputEvent(byte[] input) {

        this.input = input;
        try {
            // Get transcript from Whisper
            this.transcript = APICalls.getAudioXscript(this.input);
            // Get embedding vector for the transcript
            this.embedding = APICalls.getEmbeddedVal(transcript);
        } catch (IOException e) {
            throw new RuntimeException("Error processing input audio: " + e.getMessage(), e);
        }
    }

    /** Returns the transcript of this input */
    public String getTranscript() {
        return transcript;
    }

    /** Returns the embedding vector */
    public double[] getEmbedding() {
        return embedding;
    }


    public double getEmbeddingNorm() {
        if (embedding == null) return 0.0;
        double sum = 0.0;
        for (double v : embedding) {
            sum += v * v;
        }
        return Math.sqrt(sum);
    }

    public String toString() {
        return "InputEvent{" +
                ", transcript='" + (transcript != null ? transcript : "not transcribed") + '\'' +
                ", embedding=" + (embedding != null ? Arrays.toString(embedding) : "not embedded") +
                '}';
    }
}
