package sampling.impl;

import primitives.Point2D;

/**
 * Generates points at the center of each grid cell.
 *
 * @author David &amp; Yehuda
 */
public class GridSampler extends AbstractGridSampler {

    /**
     * Creates a grid sampler that samples the center of each cell.
     */
    public GridSampler() {
    }

    /**
     * Calculates the sample point position within the specified cell.
     *
     * @param x    origin x coordinate of the cell
     * @param y    origin y coordinate of the cell
     * @param step size of the cell
     * @return Point2D inside the cell
     */
    @Override
    protected Point2D getPoint(double x, double y, double step) {
        // Point at the center of the cell
        return new Point2D(x + 0.5 * step, y + 0.5 * step);
    }
}