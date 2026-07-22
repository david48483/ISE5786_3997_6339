package sampling.impl;

import sampling.api.Point2D;

/**
 * Generates points at the center of each grid cell.
 */
public class GridSampler extends AbstractGridSampler {

    public GridSampler() {
    }

    @Override
    protected Point2D getPoint(double x, double y, double step) {
        // נקודה במרכז התא
        return new Point2D(x + 0.5 * step, y + 0.5 * step);
    }
}