package sampling.impl;

import sampling.api.Point2D;

/**
 * Generates points with random jitter within each grid cell.
 */
public class JitteredSampler extends AbstractGridSampler {

    /**
     * Creates a jittered sampler that places a random point inside each cell.
     */
    public JitteredSampler() {
    }

    @Override
    protected Point2D getPoint(double xCell, double yCell, double step) {

        return new Point2D(
                xCell + RANDOM.nextDouble() * step,
                yCell + RANDOM.nextDouble() * step
        );
    }
}