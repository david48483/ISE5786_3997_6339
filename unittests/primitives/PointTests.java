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

    /**
     * Shared vector used in test cases.
     */
    private static final Vector VECTOR = new Vector(3, 4, 5);

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

        // TC03: Passing a zero vector is invalid and should throw an exception.
        assertThrows(IllegalArgumentException.class, () -> POINT.add(new Vector(Double3.ZERO)),
                "ERROR: Point add(Vector) with zero vector must throw");
    }

}