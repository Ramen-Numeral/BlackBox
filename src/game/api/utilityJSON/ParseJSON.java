package game.api.utilityJSON;

import java.io.*;
import java.util.Arrays;

public class ParseJSON {

    /**
     * Parses the "embedding" array from a JSON string into a double[].
     */
    public static double[] embParseResponse(String json) throws IOException {
        if (json == null || json.isEmpty()) throw new IOException("JSON response empty");

        int start = json.indexOf("[", json.indexOf("\"embedding\":"));
        int end = json.indexOf("]", start);
        if (start == -1 || end == -1) throw new IOException("Malformed embedding array");

        String[] parts = json.substring(start + 1, end).trim().split(",");
        return Arrays.stream(parts)
                .map(String::trim)
                .mapToDouble(Double::parseDouble)
                .toArray();
    }

    /**
     * Parses the "text" field from a JSON string.
     */
    public static String whisParseResponse(String json) throws IOException {
        if (json == null || json.isEmpty()) throw new IOException("JSON response empty");

        int start = json.indexOf("\"text\":\"");
        if (start == -1) throw new IOException("'text' not found");
        start += 8; // skip "text":

        int end = json.indexOf("\"", start);
        if (end == -1) throw new IOException("closing quote not found");

        return json.substring(start, end).trim();
    }

    /**
     * Reads all bytes from an InputStream and returns a byte array.
     */
    public static byte[] pollyParseResponse(InputStream in) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = in.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }
            return baos.toByteArray();
        }
    }
}
