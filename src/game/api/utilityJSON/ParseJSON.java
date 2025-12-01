package game.api.utilityJSON;
import java.io.IOException;

public class ParseJSON {

    public static double[] embParseResponse(String json) throws IOException {
        if (json == null || json.isEmpty())
            throw new IOException("JSON response empty");

        int dataIdx = json.indexOf("\"embedding\":");
        if (dataIdx == -1) throw new IOException("'embedding' not found");

        int start = json.indexOf("[", dataIdx);
        int end = json.indexOf("]", start);
        if (start == -1 || end == -1) throw new IOException("malformed embedding array");

        String arrayStr = json.substring(start + 1, end).trim();
        if (arrayStr.isEmpty()) return new double[0];

        String[] parts = arrayStr.split(",");
        double[] vector = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            vector[i] = Double.parseDouble(parts[i].trim());
        }
        return vector;
    }

    //parses the json to get the response info
    public static String whisParseResponse(String json) throws IOException {
        if (json == null || json.isEmpty())
            throw new IOException("JSON response empty");

        int start = json.indexOf("\"text\":\"");
        if (start == -1) throw new IOException("'text' not found");

        start += 8; //skip text
        int end = json.indexOf("\"", start); //get the response xscription
        if (end == -1) throw new IOException("closing quote not found");

        return json.substring(start, end).trim();
    }

}
