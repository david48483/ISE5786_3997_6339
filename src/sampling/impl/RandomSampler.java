package sampling.impl;

import sampling.api.Point2D;
import sampling.api.Sampler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A sampler that generates points randomly (stochastically) within a circular target area.
 */
public class RandomSampler implements Sampler {

    private static final Random RANDOM = new Random();

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

            if (x * x + y * y <= radiusSq) {
                points.add(new Point2D(x, y));
            }
        }

        return points;
    }
}