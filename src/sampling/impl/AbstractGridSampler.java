package sampling.impl;

import sampling.api.Point2D;
import sampling.api.Sampler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static primitives.Util.alignZero;

/**
 * An abstract base class for grid-based samplers.
 * It handles grid generation and shape filtering,
 * delegating the exact point positioning within each cell to its subclasses.
 */
public abstract class AbstractGridSampler implements Sampler {

    /**
     * Default constructor for grid-based samplers.
     */
    protected AbstractGridSampler() {
    }

    /**
     * Shared random generator used by grid sampler implementations.
     */
    protected static final Random RANDOM = new Random();

    /**
     * Target shape used to filter generated points (square or circle).
     */
    private TargetShapeType _shape = TargetShapeType.SQUARE;

    @Override
    public void setTargetShape(TargetShapeType shape) {
        this._shape = shape;
    }

    @Override
    public List<Point2D> generatePoints(int amount, double size) {
        List<Point2D> points = new ArrayList<>();

        if (amount <= 1) {
            points.add(new Point2D(0, 0));
            return points;
        }

        // Compute the number of cells on each axis
        int effectiveAmount = (int) Math.ceil(amount * Math.sqrt(4 / Math.PI));
        double step = size / effectiveAmount;
        double start = -size / 2.0;
        double radiusSq = (size / 2.0) * (size / 2.0);

        for (int i = 0; i < effectiveAmount; i++) {
            for (int j = 0; j < effectiveAmount; j++) {
                // Origin (lower-left corner) of the (i,j) cell
                double xCell = start + i * step;
                double yCell = start + j * step;

                // Get the sample point from the cell
                Point2D p = getPoint(xCell, yCell, step);
                double distSq = p.getX() * p.getX() + p.getY() * p.getY();

                if (_shape == TargetShapeType.SQUARE || (_shape == TargetShapeType.CIRCLE && alignZero(distSq - radiusSq) <= 0)) {
                    points.add(p);
                }
            }
        }
        return points;
    }

    /**
     * Calculates the sample point position within the specified cell.
     *
     * @param xCell origin x coordinate of the cell
     * @param yCell origin y coordinate of the cell
     * @param step size of the cell
     * @return Point2D inside the cell
     */
    protected abstract Point2D getPoint(double xCell, double yCell, double step);
}