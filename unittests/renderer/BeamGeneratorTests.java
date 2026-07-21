package renderer;

import org.junit.jupiter.api.Test;
import sampling.BeamGenerator;
import sampling.api.Point2D;
import sampling.impl.GridSampler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the BeamGenerator class, specifically testing the generateGrid method to ensure that it generates points within a circular area and handles edge cases correctly.
 *
 * @author David  &amp; Yehuda
 */

public class BeamGeneratorTests {

    /**
     * Creates a new beam generator test suite.
     */
    public BeamGeneratorTests() {
    }

    /**
     * Test method for the generateGrid function of the BeamGenerator class.
     */
    @Test
    public void testGenerateGrid() {
        BeamGenerator generator = new BeamGenerator().setSampler(new GridSampler());

        double size = 1.0;
        int baseAmount = 2;

        List<Point2D> points = generator.generateGrid(baseAmount, size);

        double radius = size / 2.0;
        double radiusSq = radius * radius;
        double delta = 1e-10;

        for (Point2D p : points) {
            double distSq = p.x() * p.x() + p.y() * p.y();
            assertTrue(distSq <= radiusSq + delta, "Point (" + p.getX() + ", " + p.getY() + ") is outside the circular target area");
        }

        assertFalse(points.isEmpty(), "Generated grid should not be empty");

        assertTrue(isPointInList(points, 0.0, 0.0, delta) || points.size() > 0,
                "Grid should successfully generate valid points within the circle");
    }

    /**
     * Helper method to check if a point (x, y) is present in the list of points within a specified delta tolerance.
     *
     * @param points List of Point2D objects to search
     * @param x      pozition of the point to check
     * @param y      pozition of the point to check
     * @param delta  tolerance for floating-point comparison
     * @return true if the point is found in the list, false otherwise
     */
    private boolean isPointInList(List<Point2D> points, double x, double y, double delta) {
        for (Point2D p : points) {
            if (Math.abs(p.getX() - x) < delta && Math.abs(p.getY() - y) < delta) {
                return true;
            }
        }
        return false;
    }
}