package game.api.utilityJSON;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import game.envsetup.SetEnv;

public class PostJSON {

    /**
     * Sends a POST request with JSON to the given endpoint and returns the response as a string.
     */
    public static String postJSON(String endpoint, String json) throws IOException {
        return postJSON(endpoint, json, "Authorization", "Bearer " + SetEnv.get("OPENAI_API_KEY"));
    }

    /**
     * Sends a POST request with JSON to the given endpoint with custom headers.
     * headers should be passed as key-value pairs: key1, value1, key2, value2, ...
     */
    public static String postJSON(String endpoint, String json, String... headers) throws IOException {
        URL url = new URL(endpoint);
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        // Add any additional headers
        if (headers.length % 2 != 0) {
            throw new IllegalArgumentException("Headers must be key-value pairs");
        }
        for (int i = 0; i < headers.length; i += 2) {
            connection.setRequestProperty(headers[i], headers[i + 1]);
        }

        // Write JSON body
        try (OutputStream out = connection.getOutputStream()) {
            out.write(json.getBytes("UTF-8"));
            out.flush();
        }

        // Read response
        StringBuilder response = new StringBuilder();
        try (BufferedReader buff = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = buff.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }
}
