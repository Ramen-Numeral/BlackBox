package game.api.utilityJSON;

import java.io.*;

import game.stateRoutines.envsetup.SetEnv;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.OutputFormat;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


public class AWSUtil {

    public static byte[] pollyParseResponse(InputStream in) throws IOException {
        byte[] pcmBytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = in.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }
            pcmBytes = baos.toByteArray();
        }

// write WAV file
        String outputFile = SetEnv.get("POLLY_OUTPUT_FILE");
        if (outputFile == null || outputFile.isEmpty()) {
            outputFile = "polly_response.wav";
        }

        AudioFormat format = new AudioFormat(16000, 16, 1, true, false); // 16kHz, 16-bit, mono, signed, little-endian
        try (AudioInputStream ais = new AudioInputStream(
                new ByteArrayInputStream(pcmBytes),
                format,
                pcmBytes.length / format.getFrameSize()
        )) {
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(outputFile));
        }
        return pcmBytes;
    }

    public static InputStream postPolly(String text) {
        // Build Polly client
        PollyClient polly = PollyClient.builder()
                .region(Region.of(SetEnv.get("AWS_REGION")))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        SetEnv.get("AWS_ACCESS_KEY"),
                                        SetEnv.get("AWS_SECRET_ACCESS_KEY")
                                )
                        )
                )
                .build();

        // Build the speech request
        SynthesizeSpeechRequest request = SynthesizeSpeechRequest.builder()
                .text(text)
                .voiceId(SetEnv.get("POLLY_VOICE"))
                .outputFormat(OutputFormat.PCM) //wav file
                .build();

        return polly.synthesizeSpeech(request);
    }

}

