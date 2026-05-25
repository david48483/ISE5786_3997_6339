package primitives;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Ray} class.
 * The tests verify:
 * <ul>
 * <li>Ray constructor validity</li>
 * <li>{@link Ray#origin()}</li>
 * <li>{@link Ray#direction()}</li>
 * <li>{@link Ray#getPoint(double)}</li>
 * </ul>
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 *
 * @author David
 * @author Yehuda
 */
class RayTests {

    /**
     * Default constructor for RayTests.
     */
    public RayTests() {
    }

    // ---- Error messages ----

    /**
     * Error message for {@link Ray#Ray(Point, Vector)} tests.
     */
    private static final String ERR_CONSTRUCTOR =
            "ERROR: Ray constructor failed to create the expected ray";

    /**
     * Error message for {@link Ray#direction()} tests.
     */
    private static final String ERR_DIRECTION =
            "ERROR: Ray constructor failed to normalize the direction vector";

    /**
     * Error message for {@link Ray#origin()} tests.
     */
    private static final String ERR_ORIGIN =
            "ERROR: Ray constructor failed to set the origin point correctly";

    /**
     * Error message for {@link Ray#getPoint(double)} tests.
     */
    private static final String ERR_GET_POINT =
            "ERROR: Ray getPoint() returned wrong point";

    // ---- Shared points and vectors ----

    /**
     * Origin point (1,2,3) used in ray tests.
     */
    private static final Point ORIGIN = new Point(1, 2, 3);

    /**
     * Direction vector (4,0,0) — normalizes to AXIS_X.
     */
    private static final Vector DIR = new Vector(4, 0, 0);

    /**
     * Expected point at t=2 along ray1: (1,2,3) + 2*(1,0,0) = (3,2,3).
     */
    private static final Point P_T2 = new Point(3, 2, 3);

    /**
     * Expected point at t=−2 along ray1: (1,2,3) + (−2)*(1,0,0) = (−1,2,3).
     */
    private static final Point P_T_NEG2 = new Point(-1, 2, 3);

    /**
     * Ray from ORIGIN in direction DIR (stored normalized as AXIS_X).
     */
    private static final Ray RAY = new Ray(ORIGIN, DIR);

    // ---- Tests ----

    /**
     * Test method for {@link Ray#Ray(Point, Vector)}.
     * Verifies correct ray construction and normalization of the direction vector.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Valid origin and non-zero direction — must not throw
        assertDoesNotThrow(() -> new Ray(ORIGIN, DIR), ERR_CONSTRUCTOR);
    }

    /**
     * Test method for {@link Ray#direction()}.
     * Verifies that the direction method returns the correct normalized direction vector.
     */
    @Test
    void testDirection() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Direction must equal the normalized form of the constructor argument
        assertEquals(Vector.AXIS_X, RAY.direction(), ERR_DIRECTION);
    }

    /**
     * Test method for {@link Ray#origin()}.
     * Verifies that the origin method returns the correct origin point.
     */
    @Test
    void testOrigin() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Origin must equal the point passed to the constructor
        assertEquals(ORIGIN, RAY.origin(), ERR_ORIGIN);
    }

    /**
     * Test method for {@link Ray#getPoint(double)}.
     * Verifies that the method returns the correct point at a given parameter t.
     */
    @Test
    void testGetPoint() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: t > 0 — point ahead of origin
        assertEquals(P_T2, RAY.getPoint(2), ERR_GET_POINT);

        // EP02: t < 0 — point behind origin
        assertEquals(P_T_NEG2, RAY.getPoint(-2), ERR_GET_POINT);

        // =============== Boundary Values Tests ==================

        // BVA11: t = 0 — zero displacement, must throw
        assertThrows(IllegalArgumentException.class, () -> RAY.getPoint(0), ERR_GET_POINT);
    }

    /**
     * Test method for {@link Ray#findClosestPoint(List)}.
     * Verifies that the method correctly identifies the closest point from a list of points.
     */
    @Test
    void findClosestPoint() {

        List<Point> points = List.of(
                new Point(0, 0, 7),
                new Point(0, 0, 1),
                new Point(0, 0, 4)

        );

        // ============ Equivalence Partitions Tests ==============
        //EP01  : A list of points where the closest point is in the middle of the list should return the closest point
        Ray ray = new Ray(Point.ZERO, Vector.AXIS_Z);
        Point closest = ray.findClosestPoint(points);
        assertEquals(points.get(1), closest, "ERROR: findClosetPoint() did not return the expected closest point");

        //= ============== Boundary Values Tests ==================
        //BVA01: An empty list of points should return null
        List<Point> emptyPoints = List.of();
        assertNull(ray.findClosestPoint(emptyPoints), "ERROR: findClosetPoint() should return null for an empty list of points");

        //BVA02: A list of points where the closest point is behind the ray's origin should return the closest point
        ray = new Ray(new Point(0, 0, 6), Vector.AXIS_Z);
        assertEquals(points.get(0), ray.findClosestPoint(points), "ERROR: findClosetPoint() did not return the expected closest point when the closest point is behind the ray's origin");

        //BVA03: A list of points where the closest point is ahead of the ray's origin should return the closest point
        ray = new Ray(new Point(0, 0, 3), Vector.AXIS_Z);
        assertEquals(points.get(2), ray.findClosestPoint(points), "ERROR: findClosetPoint() did not return the expected closest point when the closest point is ahead of the ray's origin");

    }
}
