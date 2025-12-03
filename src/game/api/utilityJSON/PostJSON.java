package game.api.utilityJSON;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import game.startupRoutine.envsetup.SetEnv;

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

    /** Sends a POST request with JSON and returns InputStream for binary response (Polly) */
    public static InputStream postJSONBinReturn(String endpoint, String json, String authHeader) throws IOException {
        URLConnection conn = new URL(endpoint).openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", authHeader);

        try (OutputStream out = conn.getOutputStream()) { out.write(json.getBytes("UTF-8")); }
        return conn.getInputStream();
    }
}
