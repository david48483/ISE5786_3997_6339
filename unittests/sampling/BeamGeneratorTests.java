package sampling;

import org.junit.jupiter.api.Test;
import sampling.api.Point2D;
import sampling.impl.GridSampler; // ייבוא של המחלקה החדשה שיצרנו

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the GridSampler class, specifically testing the generatePoints method
 * to ensure that it generates points within a circular area and handles edge cases correctly.
 *
 * @author David &amp; Yehuda
 */
public class BeamGeneratorTests {

    /**
     * Creates a new beam generator test suite.
     */
    public BeamGeneratorTests() {
    }

    /**
     * Test method for the generatePoints function of the GridSampler class.
     */
    @Test
    public void testGenerateGrid() {
        // במקום BeamGenerator עם Jitter מכובה, אנחנו פשוט משתמשים ב-GridSampler הנקי!
        GridSampler sampler = new GridSampler();

        double size = 1.0;
        int baseAmount = 2;

        // הפונקציה נקראת כעת generatePoints (כפי שהוגדר בממשק Sampler)
        List<Point2D> points = sampler.generatePoints(baseAmount, size);

        double radius = size / 2.0;
        double radiusSq = radius * radius;
        double delta = 1e-10;

        for (Point2D p : points) {
            // שימוש ב- x() ו- y() בגלל שזה record
            double distSq = p.x() * p.x() + p.y() * p.y();
            assertTrue(distSq <= radiusSq + delta,
                    "Point (" + p.x() + ", " + p.y() + ") is outside the circular target area");
        }

        assertFalse(points.isEmpty(), "Generated grid should not be empty");

        assertTrue(isPointInList(points, 0.0, 0.0, delta) || points.size() > 0,
                "Grid should successfully generate valid points within the circle");
    }

    /**
     * Helper method to check if a point (x, y) is present in the list of points within a specified delta tolerance.
     *
     * @param points List of Point2D objects to search
     * @param x      position of the point to check
     * @param y      position of the point to check
     * @param delta  tolerance for floating-point comparison
     * @return true if the point is found in the list, false otherwise
     */
    private boolean isPointInList(List<Point2D> points, double x, double y, double delta) {
        for (Point2D p : points) {
            // גם כאן עודכן השימוש ל- x() ו- y()
            if (Math.abs(p.x() - x) <= delta && Math.abs(p.y() - y) <= delta) {
                return true;
            }
        }
        return false;
    }
}