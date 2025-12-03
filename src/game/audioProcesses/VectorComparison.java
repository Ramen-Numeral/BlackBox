package game.audioProcesses;

public class VectorComparison {

    /**
     * Computes cosine similarity between two vectors.
     * Returns a value between -1 (opposite) and 1 (identical).
     * @param ui The first vector.
     * @param vi The second vector.
     * @return The cosine similarity value.
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
            unorm += ui[i] * ui[i];
            vnorm += vi[i] * vi[i];
        }

        // Avoid division by zero if either vector is a zero vector
        if (unorm == 0 || vnorm == 0) return 0;

        // (u . v) / (||u|| * ||v||)
        return dot / (Math.sqrt(unorm) * Math.sqrt(vnorm));
    }
}