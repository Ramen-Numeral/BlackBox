package game.languageProcessing;

public class VectorUtility {


    /**
     * Computes cosine similarity between two vectors.
     * Returns a value between -1 (opposite) and 1 (identical).
     */
    public static double cosineSimilarity(double[] ui, double[] vi) {
        if (ui == null || vi == null)
            throw new IllegalArgumentException("Vectors must not be null");
        if (ui.length != vi.length)
            throw new IllegalArgumentException("Vectors must have same length");

        double dot = 0.0;
        double unorm = 0.0;
        double vnorm = 0.0;

        for (int i = 0; i < ui.length; i++) {
            dot += ui[i] * vi[i];
            unorm += ui[i] * vi[i];
            vnorm += ui[i] * vi[i];
        }

        if (unorm == 0 || vnorm == 0) return 0; // avoid division by zero

        return dot / (Math.sqrt(unorm) * Math.sqrt(vnorm));
    }
}


