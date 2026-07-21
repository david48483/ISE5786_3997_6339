package sampling.impl;

import sampling.api.Point2D;

/**
 * Generates points in a perfect, uniform grid.
 */
public class GridSampler extends AbstractGridSampler {

    /**
     * Creates a new grid sampler instance.
     */
    public GridSampler() {
    }

    @Override
    protected Point2D getPoint(double x, double y, double step) {
        return new Point2D(x, y);
    }
}