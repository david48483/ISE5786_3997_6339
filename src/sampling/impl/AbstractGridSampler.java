package sampling.impl;

import sampling.api.Point2D;
import sampling.api.Sampler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An abstract base class for grid-based samplers.
 * It handles the grid generation and circular filtering,
 * delegating the exact point positioning to its subclasses.
 */
public abstract class AbstractGridSampler implements Sampler {

    protected static final Random RANDOM = new Random();

    @Override
    public List<Point2D> generatePoints(int amount, double size) {
        List<Point2D> points = new ArrayList<>();

        if (amount <= 1) {
            points.add(new Point2D(0, 0));
            return points;
        }

        int effectiveAmount = (int) Math.ceil(amount * Math.sqrt(4 / Math.PI));
        double step = size / (effectiveAmount - 1);
        double start = -size / 2;
        double radiusSq = (size / 2) * (size / 2);

        for (int i = 0; i < effectiveAmount; i++) {
            for (int j = 0; j < effectiveAmount; j++) {
                double x = start + i * step;
                double y = start + j * step;

                Point2D p = getPoint(x, y, step);

                if (p.getX() * p.getX() + p.getY() * p.getY() <= radiusSq) {
                    points.add(p);
                }
            }
        }
        return points;
    }

    /**
     * Calculates the final position of the sample point.
     * * @param x    The base x coordinate on the grid
     * @param y    The base y coordinate on the grid
     * @param step The size of a grid cell
     * @return The finalized Point2D
     */
    protected abstract Point2D getPoint(double x, double y, double step);
}