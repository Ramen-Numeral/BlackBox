package game.api.utilityJSON;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import game.stateRoutines.envsetup.SetEnv;

public class PostJSON {

    /** Sends a POST request with JSON and returns response as String */
    public static String postJSON(String endpoint, String json) throws IOException {
        return postJSON(endpoint, json, "Authorization", "Bearer " + SetEnv.get("OPENAI_API_KEY"));
    }

    /** Sends a POST request with JSON and custom headers */
    public static String postJSON(String endpoint, String json, String... headers) throws IOException {
        URLConnection conn = new URL(endpoint).openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        if (headers.length % 2 != 0) throw new IllegalArgumentException("Headers must be key-value pairs");
        for (int i = 0; i < headers.length; i += 2) conn.setRequestProperty(headers[i], headers[i + 1]);

        try (OutputStream out = conn.getOutputStream()) { out.write(json.getBytes("UTF-8")); }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            StringBuilder sb = new StringBuilder(); String line;
            while ((line = in.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }


    public static InputStream postPolly(String text) {
        PollyClient polly = PollyClient.builder()
                .region(Region.of(SetEnv.get("AWS_REGION")))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                SetEnv.get("AWS_ACCESS_KEY"),
                                SetEnv.get("AWS_SECRET_ACCESS_KEY")
                        )
                ))
                .build();

        SynthesizeSpeechRequest request = SynthesizeSpeechRequest.builder()
                .text(text)
                .voiceId(SetEnv.get("POLLY_VOICE"))
                .outputFormat(OutputFormat.MP3)
                .build();

        SynthesizeSpeechResponse response = polly.synthesizeSpeech(request);
        return response.audioStream();
    }
}
