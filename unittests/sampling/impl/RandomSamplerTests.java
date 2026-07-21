package sampling.impl;

import org.junit.jupiter.api.Test;
import sampling.api.Point2D;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link RandomSampler}.
 * The tests verify that points are generated purely stochastically within the circular area.
 *
 * @author David &amp; Yehuda
 */
public class RandomSamplerTests {

    /**
     * Default constructor for RandomSamplerTests.
     */
    public RandomSamplerTests() {
    }

    /**
     * A small delta for comparing floating-point numbers.
     */
    private static final double DELTA = 1e-10;

    /**
     * Test method for {@link RandomSampler#generatePoints(int, double)}.
     */
    @Test
    void testGeneratePoints() {
        RandomSampler sampler = new RandomSampler();
        double size = 4.0;
        int requestedAmount = 25;
        double radiusSq = (size / 2.0) * (size / 2.0);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Verify exact requested amount is generated
        List<Point2D> points = sampler.generatePoints(requestedAmount, size);

        assertNotNull(points, "ERROR: generatePoints() returned null");
        assertEquals(requestedAmount, points.size(), "ERROR: RandomSampler must generate the exact requested amount of points");

        // Verify points fall inside the circle
        for (Point2D p : points) {
            double distSq = p.x() * p.x() + p.y() * p.y();
            assertTrue(distSq <= radiusSq + DELTA,
                    "ERROR: Random point (" + p.x() + ", " + p.y() + ") lies outside the circular target area");
        }

        // =============== Boundary Values Tests ==================

        // TC11: Boundary value - amount = 1
        List<Point2D> singlePoint = sampler.generatePoints(1, size);
        assertEquals(1, singlePoint.size(), "ERROR: generatePoints(1) should return exactly 1 point");
        assertEquals(0.0, singlePoint.getFirst().x(), DELTA, "ERROR: Center point X coordinate should be 0");
        assertEquals(0.0, singlePoint.getFirst().y(), DELTA, "ERROR: Center point Y coordinate should be 0");
    }
}