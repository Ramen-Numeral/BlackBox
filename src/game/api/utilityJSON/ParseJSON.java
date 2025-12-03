package game.api.utilityJSON;

import java.io.*;

public class ParseJSON {

    public static double[] embParseResponse(String json) throws IOException {
        if (json == null || json.isEmpty()) throw new IOException("JSON response empty");
        int start = json.indexOf("[", json.indexOf("\"embedding\":"));
        int end = json.indexOf("]", start);
        if (start == -1 || end == -1) throw new IOException("Malformed embedding array");

        String[] parts = json.substring(start + 1, end).trim().split(",");
        double[] vector = new double[parts.length];
        for (int i = 0; i < parts.length; i++) vector[i] = Double.parseDouble(parts[i].trim());
        return vector;
    }

    public static String whisParseResponse(String json) throws IOException {
        if (json == null || json.isEmpty()) throw new IOException("JSON response empty");
        int start = json.indexOf("\"text\":\"");
        if (start == -1) throw new IOException("'text' not found");

        start += 8; // skip "text":
        int end = json.indexOf("\"", start);
        if (end == -1) throw new IOException("closing quote not found");

        return json.substring(start, end).trim();
    }

    public static byte[] pollyParseResponse(InputStream in) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buf = new byte[1024];
            int n;

            while ((n = in.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }

            // 3. Return the collected bytes
            return baos.toByteArray();
        }
    }
}
