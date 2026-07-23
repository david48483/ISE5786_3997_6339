package sampling.impl;

import org.junit.jupiter.api.Test;
import primitives.Point2D;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link JitteredSampler}.
 * Verifies that jittered points are generated within the specified circular or square boundaries,
 * and that randomness (jittering) is applied correctly.
 *
 * @author David &amp; Yehuda
 */
public class JitteredSamplerTests {

    /**
     * Default constructor for JitteredSamplerTests.
     */
    public JitteredSamplerTests() {
    }

    /**
     * A small delta for comparing floating-point numbers.
     */
    private static final double DELTA = 1e-10;

    /**
     * Test method for {@link JitteredSampler#generatePoints(int, double)}.
     * Tests point generation for both CIRCLE and SQUARE target shapes.
     */
    @Test
    void testGeneratePoints() {
        JitteredSampler sampler = new JitteredSampler();
        double size = 2.0;
        double halfSize = size / 2.0;
        double radiusSq = halfSize * halfSize;
        int amount = 16;

        // ============ Equivalence Partitions Tests ==============

        // TC01: Circular Target Shape
        sampler.setTargetShape(TargetShapeType.CIRCLE);
        List<Point2D> circlePoints = sampler.generatePoints(amount, size);

        assertNotNull(circlePoints, "ERROR: generatePoints() returned null for CIRCLE shape");
        assertFalse(circlePoints.isEmpty(), "ERROR: generatePoints() returned an empty list for CIRCLE shape");

        // Verify all generated points are strictly within the target circle radius
        for (Point2D p : circlePoints) {
            double distSq = p.x() * p.x() + p.y() * p.y();
            assertTrue(distSq <= radiusSq + DELTA,
                    "ERROR: Jittered point (" + p.x() + ", " + p.y() + ") lies outside the circular radius");
        }

        // TC02: Square Target Shape
        sampler.setTargetShape(TargetShapeType.SQUARE);
        List<Point2D> squarePoints = sampler.generatePoints(amount, size);

        assertNotNull(squarePoints, "ERROR: generatePoints() returned null for SQUARE shape");
        assertFalse(squarePoints.isEmpty(), "ERROR: generatePoints() returned an empty list for SQUARE shape");

        // Verify all generated points lie within the bounding box [-halfSize, halfSize]
        for (Point2D p : squarePoints) {
            assertTrue(Math.abs(p.x()) <= halfSize + DELTA && Math.abs(p.y()) <= halfSize + DELTA,
                    "ERROR: Jittered point (" + p.x() + ", " + p.y() + ") lies outside the square boundary");
        }

        // TC03: Randomness test - two consecutive runs should produce different values due to jittering
        List<Point2D> pointsRun2 = sampler.generatePoints(amount, size);
        boolean isDifferent = false;
        for (int i = 0; i < squarePoints.size(); i++) {
            if (Math.abs(squarePoints.get(i).x() - pointsRun2.get(i).x()) > DELTA ||
                    Math.abs(squarePoints.get(i).y() - pointsRun2.get(i).y()) > DELTA) {
                isDifferent = true;
                break;
            }
        }
        assertTrue(isDifferent, "ERROR: JitteredSampler should generate different randomized points on consecutive runs");

        // =============== Boundary Values Tests ==================

        // TC11: Boundary value - amount = 1 (should return single center point (0,0))
        List<Point2D> singlePoint = sampler.generatePoints(1, size);
        assertEquals(1, singlePoint.size(), "ERROR: generatePoints(1) should return exactly 1 point");
        assertEquals(0.0, singlePoint.getFirst().x(), DELTA, "ERROR: Center point X coordinate should be 0");
        assertEquals(0.0, singlePoint.getFirst().y(), DELTA, "ERROR: Center point Y coordinate should be 0");

        // TC12: Boundary value - amount <= 0 (should default to single center point)
        List<Point2D> zeroPointList = sampler.generatePoints(0, size);
        assertEquals(1, zeroPointList.size(), "ERROR: generatePoints(0) should return 1 default center point");
    }
}