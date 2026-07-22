package sampling.impl;

import sampling.api.Point2D;

/**
 * Generates points with random jitter within each grid cell.
 */
public class JitteredSampler extends AbstractGridSampler {

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