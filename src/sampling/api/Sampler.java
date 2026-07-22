package sampling.api;

import java.util.List;

import sampling.impl.TargetShapeType;

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
    List<Point2D> generatePoints(int amount, double size);

    /**
     * Sets the geometric shape of the target area in which points are generated.
     * Supported shapes are defined by {@link TargetShapeType}.
     *
     * @param shape the target shape type (e.g., CIRCLE or SQUARE)
     */
    void setTargetShape(TargetShapeType shape);
}
