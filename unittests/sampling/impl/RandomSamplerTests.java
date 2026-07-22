package sampling.impl;

import org.junit.jupiter.api.Test;
import sampling.api.Point2D;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link RandomSampler}.
 * The tests verify that points are generated purely stochastically within circular or square areas.
 *
 * @author David &amp; Yehuda
 */
public class RandomSamplerTests {

    /**
     * Default constructor for the test class.
     */
    public RandomSamplerTests() {
    }

    /**
     * Numerical tolerance used in floating-point assertions.
     */
    private static final double DELTA = 1e-10;

    /**
     * Verifies that RandomSampler generates points inside the selected shape,
     * returns the exact requested amount (except boundary cases), and differs between runs.
     */
    @Test
    void testGeneratePoints() {
        RandomSampler sampler = new RandomSampler();
        double size = 4.0;
        int requestedAmount = 25;
        double radiusSq = (size / 2.0) * (size / 2.0);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Circular Target Shape
        sampler.setTargetShape(TargetShapeType.CIRCLE);
        List<Point2D> circlePoints = sampler.generatePoints(requestedAmount, size);

        assertNotNull(circlePoints, "ERROR: generatePoints() returned null");
        assertEquals(requestedAmount, circlePoints.size(), "ERROR: RandomSampler must generate the exact requested amount of points");

        for (Point2D p : circlePoints) {
            double distSq = p.x() * p.x() + p.y() * p.y();
            assertTrue(distSq <= radiusSq + DELTA,
                    "ERROR: Random point (" + p.x() + ", " + p.y() + ") lies outside the circular target area");
        }

        // TC02: Square Target Shape
        sampler.setTargetShape(TargetShapeType.SQUARE);
        List<Point2D> squarePoints = sampler.generatePoints(requestedAmount, size);

        assertEquals(requestedAmount, squarePoints.size(), "ERROR: RandomSampler must generate exact requested amount for SQUARE shape");
        for (Point2D p : squarePoints) {
            assertTrue(Math.abs(p.x()) <= size / 2.0 + DELTA && Math.abs(p.y()) <= size / 2.0 + DELTA,
                    "ERROR: Random point (" + p.x() + ", " + p.y() + ") lies outside the square target area");
        }

        // TC03: Randomness Test
        List<Point2D> run2Points = sampler.generatePoints(requestedAmount, size);
        boolean isDifferent = false;
        for (int i = 0; i < squarePoints.size(); i++) {
            if (Math.abs(squarePoints.get(i).x() - run2Points.get(i).x()) > DELTA ||
                    Math.abs(squarePoints.get(i).y() - run2Points.get(i).y()) > DELTA) {
                isDifferent = true;
                break;
            }
        }
        assertTrue(isDifferent, "ERROR: RandomSampler should generate different points on consecutive runs");

        // =============== Boundary Values Tests ==================

        // TC11: Boundary value - amount = 1
        List<Point2D> singlePoint = sampler.generatePoints(1, size);
        assertEquals(1, singlePoint.size(), "ERROR: generatePoints(1) should return exactly 1 point");
        assertEquals(0.0, singlePoint.getFirst().x(), DELTA, "ERROR: Center point X coordinate should be 0");
        assertEquals(0.0, singlePoint.getFirst().y(), DELTA, "ERROR: Center point Y coordinate should be 0");

        // TC12: Boundary value - amount <= 0
        List<Point2D> zeroPointList = sampler.generatePoints(0, size);
        assertEquals(1, zeroPointList.size(), "ERROR: generatePoints(0) should return 1 default center point");
    }
}