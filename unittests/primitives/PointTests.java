package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test class for {@link Point}.
 * The tests verify:
 * <ul>
 * <li>{@link Point#add(Vector)}</li>
 * <li>{@link Point#subtract(Point)}</li>
 * <li>{@link Point#distance(Point)}</li>
 * <li>{@link Point#distanceSquared(Point)}</li>
 * </ul>
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 *
 * @author David
 * @author Yehuda
 */
class PointTests {
    /**
     * Shared point used in test cases.
     */
    private static final Point POINT = new Point(3, 4, 5);

    private static final Point POINT2 = new Point(6, 10, 7);
    /**
     * Shared vector used in test cases.
     */
    private static final Vector VECTOR = new Vector(3, 4, 5);

    private static final double DELTA = 1e-6;

    /**
     * Default constructor to satisfy JavaDoc generator.
     */
    PointTests() {
        // Empty by design.
    }

    /**
     * Test method for {@link Point#add(Vector)}.
     * Verifies that adding a vector to a point results in the expected translated point.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Adding a vector to a point should yield the expected translated point.
        assertEquals(new Point(6, 8, 10), POINT.add(VECTOR),
                "ERROR: Point add(Vector) failed");

        // =============== Boundary Values Tests ==================
        // TC02: Adding an inverse vector should yield the origin.
        assertEquals(Point.ZERO, POINT.add(new Vector(-3, -4, -5)),
                "ERROR: Point add(Vector) with inverse vector failed");

    }

    /**
     * Test method for {@link Point#subtract(Point)}.
     * Verifies that subtracting one point from another results in the expected vector.
     */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Subtracting a point from itself should yield the zero vector.
//        assertEquals(Point.ZERO, POINT.subtract(POINT),//rerurn vector, it can't return 000
//                "ERROR: Point subtract(Point) with itself failed");

        // TC02: Subtracting a different point should yield the expected vector.
        assertEquals(new Vector(-3, -6, -2), POINT.subtract(POINT2),
                "ERROR: Point subtract(Point) with different point failed");

        //TC03: Subtracting a point from itself should yield the zero vector.
        assertThrows(IllegalArgumentException.class, () -> POINT.subtract(POINT),
                "ERROR: Point subtract(Point) with itself should throw an exception");

    }

    /**
     * Test method for {@link Point#distanceSquared(Point)}.
     * Verifies that the squared distance between two points is calculated correctly.
     */
    @Test
    void testDistanceSquared() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Squared distance between a point and itself should be zero.
        assertEquals(0, POINT.distanceSquared(POINT), DELTA,
                "ERROR: Point distanceSquared(Point) with itself failed");

        //TC02:  Squared distance between a point and a different point should be calculated correctly.
        assertEquals(49, POINT.distanceSquared(POINT2), DELTA,
                "ERROR: Point distanceSquared(Point) with different point failed");
    }

    /**
     * Test method for {@link Point#distance(Point)}.
     * Verifies that the distance between two points is calculated correctly.
     */
    @Test
    void testDistance() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Distance between two different points should be calculated correctly.
        assertEquals(7, POINT.distance(POINT2), DELTA,
                "ERROR: Point distance(Point) with different point failed");
    }

}