package sampling.impl;

import sampling.api.Point2D;
import sampling.api.Sampler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static primitives.Util.alignZero;

/**
 * A sampler that generates points randomly (stochastically) within a circular target area.
 */
public class RandomSampler implements Sampler {

    /**
     * Creates a new random sampler instance.
     */
    public RandomSampler() {
    }

    /**
     * Random number generator for point generation.
     */
    private static final Random RANDOM = new Random();

    /**
     * The shape used to limit random points (inside a circle or within a square box).
     */
    private TargetShapeType _shape = TargetShapeType.SQUARE;

    @Override
    public void setTargetShape(TargetShapeType shape){
        this._shape = shape;
    }

    @Override
    public List<Point2D> generatePoints(int amount, double size) {
        List<Point2D> points = new ArrayList<>();

        if (amount <= 1) {
            points.add(new Point2D(0, 0));
            return points;
        }

        double radius = size / 2.0;
        double radiusSq = radius * radius;

        while (points.size() < amount) {
            double x = (RANDOM.nextDouble() * size) - radius;
            double y = (RANDOM.nextDouble() * size) - radius;

            double distSq = x * x + y * y;
            if (_shape == TargetShapeType.SQUARE || (_shape == TargetShapeType.CIRCLE && alignZero(distSq - radiusSq) <= 0)) {
                points.add(new Point2D(x, y));
            }
        }

        return points;
    }
}