package game.audioUtil;

import java.util.stream.IntStream;

public class VectorComparison {

  //helper for embedding comparison
    public static double cosineSimilarity(double[] ui, double[] vi) {
        if (ui == null || vi == null)
            throw new IllegalArgumentException("Vectors must not be null");
        if (ui.length != vi.length)
            throw new IllegalArgumentException("Vectors must have same length");

        double dot = IntStream.range(0, ui.length)
                .mapToDouble(i -> ui[i] * vi[i])
                .sum();

        double unorm = IntStream.range(0, ui.length)
                .mapToDouble(i -> ui[i] * ui[i])
                .sum();

        double vnorm = IntStream.range(0, ui.length)
                .mapToDouble(i -> vi[i] * vi[i])
                .sum();

        if (unorm == 0 || vnorm == 0) return 0;

        return dot / (Math.sqrt(unorm) * Math.sqrt(vnorm));
    }
}
