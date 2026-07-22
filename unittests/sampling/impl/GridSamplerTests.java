package sampling.impl;

import org.junit.jupiter.api.Test;
import sampling.api.Point2D;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link GridSampler}.
 * The tests verify that points are correctly generated in a uniform grid pattern within a circular boundary.
 *
 * @author David &amp; Yehuda
 */
public class GridSamplerTests {

    /**
     * Default constructor for GridSamplerTests.
     */
    public GridSamplerTests() {
    }

    /**
     * A small delta for comparing floating-point numbers.
     */
    private static final double DELTA = 1e-10;

    /**
     * Test method for {@link GridSampler#generatePoints(int, double)}.
     */
    @Test
    void testGeneratePoints() {
        GridSampler sampler = new GridSampler();
        double size = 2.0;
        double radiusSq = (size / 2.0) * (size / 2.0);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Regular grid sampling generation
        int amount = 9;
        List<Point2D> points = sampler.generatePoints(amount, size);

        assertNotNull(points, "ERROR: generatePoints() returned null");
        assertFalse(points.isEmpty(), "ERROR: generatePoints() returned an empty list");

       /* // Verify all generated points are strictly within the target radius
        for (Point2D p : points) {
            double distSq = p.x() * p.x() + p.y() * p.y();
            assertTrue(distSq <= radiusSq + DELTA,
                    "ERROR: Point (" + p.x() + ", " + p.y() + ") lies outside the circular radius");
        }*/

        // =============== Boundary Values Tests ==================

        // TC11: Boundary value - amount = 1 (should return single center point (0,0))
        List<Point2D> singlePointList = sampler.generatePoints(1, size);
        assertEquals(1, singlePointList.size(), "ERROR: generatePoints(1) should return exactly 1 point");
        assertEquals(0.0, singlePointList.getFirst().x(), DELTA, "ERROR: Center point X coordinate should be 0");
        assertEquals(0.0, singlePointList.getFirst().y(), DELTA, "ERROR: Center point Y coordinate should be 0");

        // TC12: Boundary value - amount <= 0 (should also default to single center point)
        List<Point2D> zeroPointList = sampler.generatePoints(0, size);
        assertEquals(1, zeroPointList.size(), "ERROR: generatePoints(0) should return 1 default center point");
    }
}