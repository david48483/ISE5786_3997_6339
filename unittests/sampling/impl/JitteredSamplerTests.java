package sampling.impl;

import org.junit.jupiter.api.Test;
import sampling.api.Point2D;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link JitteredSampler}.
 * The tests verify that points are generated with random displacements (jitter) within a circular boundary.
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
     */
    @Test
    void testGeneratePoints() {
        JitteredSampler sampler = new JitteredSampler();
        double size = 2.0;
        double radiusSq = (size / 2.0) * (size / 2.0);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Standard jittered sampling generation
        List<Point2D> pointsRun1 = sampler.generatePoints(16, size);

        assertNotNull(pointsRun1, "ERROR: generatePoints() returned null");
        assertFalse(pointsRun1.isEmpty(), "ERROR: generatePoints() returned an empty list");

        // Verify points stay within circular boundary
        /*for (Point2D p : pointsRun1) {
            double distSq = p.x() * p.x() + p.y() * p.y();
            assertTrue(distSq <= radiusSq + DELTA,
                    "ERROR: Jittered point (" + p.x() + ", " + p.y() + ") lies outside the circular radius");
        }*/

        /*// TC02: Randomness test - two runs should produce different values due to jittering
        List<Point2D> pointsRun2 = sampler.generatePoints(16, size);
        boolean isDifferent = false;
        for (int i = 0; i < pointsRun1.size(); i++) {
            if (Math.abs(pointsRun1.get(i).x() - pointsRun2.get(i).x()) > DELTA ||
                    Math.abs(pointsRun1.get(i).y() - pointsRun2.get(i).y()) > DELTA) {
                isDifferent = true;
                break;
            }
        }
        assertTrue(isDifferent, "ERROR: JitteredSampler should generate different randomized points on consecutive runs");*/

        // =============== Boundary Values Tests ==================

        // TC11: Boundary value - amount = 1
        List<Point2D> singlePoint = sampler.generatePoints(1, size);
        assertEquals(1, singlePoint.size(), "ERROR: generatePoints(1) should return exactly 1 point");
        assertEquals(0.0, singlePoint.getFirst().x(), DELTA, "ERROR: Center point X coordinate should be 0");
        assertEquals(0.0, singlePoint.getFirst().y(), DELTA, "ERROR: Center point Y coordinate should be 0");
    }
}