package geometries;

import geometries.impl.Plane;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link Plane}.
 * The tests verify:
 * <ul>
 * <li>Plane constructor validity</li>
 * <li>{@link Plane#getNormal(Point)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 *
 * @author David &amp; Yehuda
 */

public class PlaneTests {

    /**
     * Default constructor for PlaneTests.
     */
    public PlaneTests() {
    }

    /**
     * A small delta for comparing floating-point numbers
     */
    private static final double DELTA = 1e-6;

    /**
     * Points used in the tests
     */
    private static final Point P101 = new Point(1, 0, 1);

    /**
     * Another point used in the tests
     */
    private static final Point P011 = new Point(0, 1, 1);

    private static final Point P001 = new Point(0, 0, 1);

    /**
     * A vector used in the tests
     */
    private static final Vector V1 = new Vector(3, 4, 5);

    /**
     * Test method for {@link Plane#Plane(Point, Point, Point)}.
     * validate that the constructor correctly creates a plane from three non-collinear points,
     * and that it throws an exception when the points are collinear or when two or more points are the same.
     */

    @Test
    void testConstructor() {

        // ============ Equivalence Partitions Tests ==============

        //TC01 check constructor, different points
        assertDoesNotThrow(() -> new Plane(P001, P101, P011),
                "ERROR,Plane constructor failed to create the expected plane  ");

        // =============== Boundary Values Tests ==================

        //TC11 First and second points are the same.
        assertThrows(IllegalArgumentException.class, () -> new Plane(P001, P001, P011),
                "ERROR: Plane constructor should throw exception when first and second points are the same");

        //   First and third points are the same.
        assertThrows(IllegalArgumentException.class, () -> new Plane(P001, P101, P001),
                "ERROR: Plane constructor should throw exception when first and third points are the same");

        //   Second and third points are the same.
        assertThrows(IllegalArgumentException.class, () -> new Plane(P001, P101, P101),
                "ERROR: Plane constructor should throw exception when second and third points are the same");

        //  All three points are different but collinear.
        assertThrows(IllegalArgumentException.class, () -> new Plane(P001, P001, P001),
                "ERROR: Plane constructor should throw exception when all three points are different but collinear");

        //   All three points are the same collinear.
        assertThrows(IllegalArgumentException.class, () -> new Plane(P101, new Point(4, 0, 1), new Point(2, 0, 1)),
                "ERROR: Plane constructor should throw exception when all three points are the same collinear");

    }

    /**
     * Test method for {@link Plane#Plane(Point, Vector)}.
     * validate that the constructor correctly creates a plane from a point and a normal vector,
     * and that it throws an exception when the normal vector is the zero vector.
     */
    @Test
    void testConstructorB() {

        //idea...
        testGetNormal();

        Plane plane = new Plane(P101, V1);

        //  ============ Equivalence Partitions Tests ==============

        //  TC01 check constructor.
        assertEquals(1, plane.getNormal(P101).length(), DELTA,
                "ERROR: Plane constructor failed to create the expected plane with point and normal vector");

    }

    /**
     * Test method for {@link Plane#getNormal(Point)}.
     * validate that the method returns the correct normal vector for points on the plane.
     */
    @Test
    void testGetNormal() {

        Plane plane = new Plane(P101, P011, P001);

        // ============ Equivalence Partitions Tests ==============

        // TC01: A point that is not the reference
        assertEquals(1, plane.getNormal(new Point(1, 5, 1)).length(), DELTA,
                "getNormal() wrong result for a point on the plane (not reference point)");

        //  check correct normal
        assertEquals(0, plane.getNormal(new Point(3, 4, 1)).dotProduct(Vector.AXIS_X),
                "ERROR: getNormal() should throw exception for a point not on the plane");

        assertEquals(0, plane.getNormal(new Point(3, 4, 1)).dotProduct(Vector.AXIS_Y),
                "ERROR: getNormal() should throw exception for a point not on the plane");

        //== Boundary Values Tests ==================
        // TC11: A point that is  reference
        assertEquals(1, plane.getNormal(P101).length(), DELTA,
                "getNormal() wrong result for a point on the plane (not reference point)");

        //  check correct normal
        assertThrows(IllegalArgumentException.class, () -> plane.getNormal(P101).crossProduct(Vector.AXIS_Z),
                "ERROR: getNormal() should throw exception for a point not on the plane");

        assertTrue(plane.getNormal(P101).dotProduct(V1) > 0,
                "ERROR: getNormal() should return a normal vector that is in the another direction as the normal vector used in the constructor");
    }

    /**
     * Test method for {@link Plane#findIntersections(Ray)}.
     * validate that the method returns the correct intersection points for rays that intersect the plane in different ways, including rays that are parallel to the plane and rays that do not intersect the plane at all.
     */
    @Test
    void testFindIntersections() {
        Plane plane = new Plane(Point.ZERO, Vector.AXIS_Z);

        Point p1 = new Point(3, 5, 0);

        Vector v1 = new Vector(1, 2, 4);

        Point p110 = new Point(1, 1, 0);

        Point p111 = new Point(1, 1, 1);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray intersects the plane (1 point)
        assertEquals(java.util.List.of(p1), plane.findIntersections(new Ray(new Point(2, 3, -4), v1)),
                "ERROR: Plane findIntersections() wrong number of points for a ray that intersects the plane");

        // EP02: Ray's line intersects the plane, but the ray points away from it (0 points)
        assertNull(plane.findIntersections(new Ray(p111, v1)),
                "ERROR: Plane findIntersections() wrong number of points for a ray that points away from the plane");

        // =============== Boundary Values Tests ==================

        // **** Group 1: Ray's line is parallel to the plane
        // BV11: Ray is parallel and included in the plane (0 points)
        assertNull(plane.findIntersections(new Ray(p110, Vector.AXIS_X)),
                "ERROR: Plane findIntersections()  wrong number of points for a ray that is parallel and included in the plane");

        //BV12 :ray is parallel and not included in the plane (0 points)
        assertNull(plane.findIntersections(new Ray(p111, Vector.AXIS_X)),
                "ERROR: Plane findIntersections() wrong number of points for a ray that is parallel and not included in the plane");

        // **** Group 2: Ray's line is orthogonal to the plane
        // BV21: Ray is orthogonal to the plane and starts before the plane (1 point)
        assertEquals(java.util.List.of(p110), plane.findIntersections(new Ray(new Point(1, 1, -1), Vector.AXIS_Z)),
                "ERROR: Plane findIntersections() wrong number of points for a ray that is orthogonal to the plane and starts before the plane");

        // BV22: Ray is orthogonal to the plane and starts in the plane (0 points)
        assertNull(plane.findIntersections(new Ray(p110, Vector.AXIS_Z)),
                "ERROR: Plane findIntersections() wrong number of points for a ray that is orthogonal to the plane and starts in the plane");

        // BV23: Ray is orthogonal to the plane and starts after the plane (0 points)
        assertNull(plane.findIntersections(new Ray(p111, Vector.AXIS_Z)),
                "ERROR: Plane findIntersections() wrong number of points for a ray that is orthogonal to the plane and starts after the plane");

        // **** Group 3: Ray's line is not parallel and not orthogonal to the plane, start in plane
        //  BV31: Ray starts in the plane (0 points)
        assertNull(plane.findIntersections(new Ray(p110, v1)),
                "ERROR: Plane findIntersections() wrong number of points for a ray that starts in the plane");

        // **** Group 4: Ray's line start in the reference point of the plane
        //  BV41: Ray starts in the reference point of the plane (0 points)
        assertNull(plane.findIntersections(new Ray(Point.ZERO, v1)),
                "ERROR: Plane findIntersections() wrong number of points for a ray that starts in the reference point of the plane");

    }
}
