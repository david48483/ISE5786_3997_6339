package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link Point}.
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
     * Default constructor to satisfy JavaDoc generator.
     */
    PointTests() {
    }

    // ---- Error messages ----

    /** Error message for {@link Point#add(Vector)} tests. */
    private static final String ERR_ADD =
            "ERROR: Point add(Vector) failed";

    /** Error message for {@link Point#subtract(Point)} tests. */
    private static final String ERR_SUBTRACT =
            "ERROR: Point subtract(Point) failed";

    /** Error message for {@link Point#distanceSquared(Point)} tests. */
    private static final String ERR_DISTANCE_SQ =
            "ERROR: Point distanceSquared(Point) failed";

    /** Error message for {@link Point#distance(Point)} tests. */
    private static final String ERR_DISTANCE =
            "ERROR: Point distance(Point) failed";

    // ---- Numeric precision ----

    /**
     * Delta value for accuracy when comparing floating-point numbers.
     */
    private static final double DELTA = 1e-6;

    // ---- Shared points and vectors ----

    /**
     * Primary test point (3,4,5).
     */
    private static final Point P1 = new Point(3, 4, 5);

    /**
     * Secondary test point (6,10,7) — distance 7 from P1.
     */
    private static final Point P2 = new Point(6, 10, 7);

    /**
     * Translation vector (3,4,5) — equals the coordinates of P1.
     */
    private static final Vector V = new Vector(3, 4, 5);

    /**
     * Negation of V: (−3,−4,−5) — adding to P1 yields the origin.
     */
    private static final Vector V_NEG = new Vector(-3, -4, -5);

    /**
     * Expected result of P1 + V: (6,8,10).
     */
    private static final Point P1_PLUS_V = new Point(6, 8, 10);

    /**
     * Expected result of P1 − P2: (−3,−6,−2).
     */
    private static final Vector P1_MINUS_P2 = new Vector(-3, -6, -2);

    // ---- Tests ----

    /**
     * Test method for {@link Point#add(Vector)}.
     * Verifies that adding a vector to a point results in the expected translated point.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Adding a vector to a point yields the expected translated point
        assertEquals(P1_PLUS_V, P1.add(V), ERR_ADD);

        // =============== Boundary Values Tests ==================

        // BVA11: Adding the inverse vector yields the origin
        assertEquals(Point.ZERO, P1.add(V_NEG), ERR_ADD);
    }

    /**
     * Test method for {@link Point#subtract(Point)}.
     * Verifies that subtracting one point from another results in the expected vector.
     */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Subtracting a different point yields the expected vector
        assertEquals(P1_MINUS_P2, P1.subtract(P2), ERR_SUBTRACT);

        // =============== Boundary Values Tests ==================

        // BVA11: Subtracting a point from itself produces zero vector — must throw
        assertThrows(IllegalArgumentException.class, () -> P1.subtract(P1), ERR_SUBTRACT);
    }

    /**
     * Test method for {@link Point#distanceSquared(Point)}.
     * Verifies that the squared distance between two points is calculated correctly.
     */
    @Test
    void testDistanceSquared() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Squared distance between two different points
        assertEquals(49, P1.distanceSquared(P2), DELTA, ERR_DISTANCE_SQ);

        // =============== Boundary Values Tests ==================

        // BVA11: Squared distance from a point to itself must be zero
        assertEquals(0, P1.distanceSquared(P1), DELTA, ERR_DISTANCE_SQ);
    }

    /**
     * Test method for {@link Point#distance(Point)}.
     * Verifies that the distance between two points is calculated correctly.
     */
    @Test
    void testDistance() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Distance between two different points
        assertEquals(7, P1.distance(P2), DELTA, ERR_DISTANCE);

        // =============== Boundary Values Tests ==================

        // BVA11: Distance from a point to itself must be zero
        assertEquals(0, P1.distance(P1), DELTA, ERR_DISTANCE);
    }

}