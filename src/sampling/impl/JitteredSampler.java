package sampling.impl;

import sampling.api.Point2D;

/**
 * Generates points in a grid with random displacements (jittering).
 */
public class JitteredSampler extends AbstractGridSampler {

    @Override
    protected Point2D getPoint(double x, double y, double step) {

        return new Point2D(
                x + (RANDOM.nextDouble() - 0.5) * step,
                y + (RANDOM.nextDouble() - 0.5) * step
        );
    }
}