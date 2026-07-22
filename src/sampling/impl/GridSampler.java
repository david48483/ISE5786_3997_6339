package sampling.impl;

import sampling.api.Point2D;

/**
 * Generates points at the center of each grid cell.
 */
public class GridSampler extends AbstractGridSampler {

    /**
     * Creates a grid sampler that samples the center of each cell.
     */
    public GridSampler() {
    }

    @Override
    protected Point2D getPoint(double x, double y, double step) {
        // Point at the center of the cell
        return new Point2D(x + 0.5 * step, y + 0.5 * step);
    }
}