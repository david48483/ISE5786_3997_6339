package sampling.api;

import java.util.List;

/**
 * Interface for sampling strategies that generate a collection of 2D points.
 * Implementations define how points are distributed within a given area.
 */
public interface Sampler {
    /**
     * Generates a list of 2D points within a target area.
     *
     * @param amount The requested number of points
     * @param size   The size (diameter/length) of the target area
     * @return List of generated 2D points
     */

    boolean circle = false;

    List<Point2D> generatePoints(int amount, double size);
}
