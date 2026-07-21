package sampling.api;

import java.util.List;

public interface Sampler {
    /**
     * Generates a list of 2D points within a target area.
     *
     * @param amount The requested number of points
     * @param size   The size (diameter/length) of the target area
     * @return List of generated 2D points
     */
    List<Point2D> generatePoints(int amount, double size);
}
