package sampling.impl;

import primitives.Point2D;

/**
 * Generates points with random jitter within each grid cell.
 *
 * @author David &amp; Yehuda
 */
public class JitteredSampler extends AbstractGridSampler {

    /**
     * Creates a jittered sampler that places a random point inside each cell.
     */
    public JitteredSampler() {
    }

    /**
     * Calculates the sample point position within the specified cell.
     *
     * @param xCell origin x coordinate of the cell
     * @param yCell origin y coordinate of the cell
     * @param step  size of the cell
     * @return Point2D inside the cell
     */
    @Override
    protected Point2D getPoint(double xCell, double yCell, double step) {
        return new Point2D(
                xCell + RANDOM.nextDouble() * step,
                yCell + RANDOM.nextDouble() * step
        );
    }
}