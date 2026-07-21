package primitives;

import org.junit.jupiter.api.Test;
import sampling.api.Point2D;

/**
 * Unit tests for the Point2D class, which represents a point in 2D space.
 *
 * @author David  &amp; Yehuda
 */

public class Point2DTests {

    /**
     * Creates a new Point2D test suite.
     */
    public Point2DTests() {
    }

    /**
     * A Point2D instance used for testing the methods of the Point2D class.
     */
    Point2D point2D = new Point2D(1, 2);

    /**
     * Tests the getX() method of the Point2D class to ensure it returns the correct x-coordinate.
     */
    @Test
    void testGetX() {
        assert point2D.getX() == 1;
    }

    /**
     * Tests the getY() method of the Point2D class to ensure it returns the correct y-coordinate.
     */
    @Test
    void testGetY() {
        assert point2D.getY() == 2;
    }

    /**
     * Tests the toString() method of the Point2D class to ensure it returns the correct string representation of the point.
     */

    @Test
    void testToString() {
        assert point2D.toString().equals("(1.0, 2.0)");
    }

}
