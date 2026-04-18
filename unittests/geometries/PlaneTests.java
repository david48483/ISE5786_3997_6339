package geometries;

import geometries.impl.Plane;
import org.junit.jupiter.api.Test;
import primitives.Point;
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
    private static final Point P2 = new Point(1, 0, 0);

    /**
     * Another point used in the tests
     */
    private static final Point P3 = new Point(0, 1, 0);

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
        assertDoesNotThrow(() -> new Plane(Point.ZERO, P2, P3),
                "ERROR,Plane constructor failed to create the expected plane  ");

        // =============== Boundary Values Tests ==================

        //TC11 First and second points are the same.
        assertThrows(IllegalArgumentException.class, () -> new Plane(P2, P2, P3),
                "ERROR: Plane constructor should throw exception when first and second points are the same");

        //   First and third points are the same.
        assertThrows(IllegalArgumentException.class, () -> new Plane(P2, P3, P2),
                "ERROR: Plane constructor should throw exception when first and third points are the same");

        //   Second and third points are the same.
        assertThrows(IllegalArgumentException.class, () -> new Plane(P3, P2, P2),
                "ERROR: Plane constructor should throw exception when second and third points are the same");

        //  All three points are different but collinear.
        assertThrows(IllegalArgumentException.class, () -> new Plane(P2, P2, P2),
                "ERROR: Plane constructor should throw exception when all three points are different but collinear");

        //   All three points are the same collinear.
        assertThrows(IllegalArgumentException.class, () -> new Plane(P2, new Point(4, 0, 0), Point.ZERO),
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

        Plane plane = new Plane(P2, V1);

        //  ============ Equivalence Partitions Tests ==============

        //  TC01 check constructor.
        assertEquals(1, plane.getNormal(P2).length(), DELTA,
                "ERROR: Plane constructor failed to create the expected plane with point and normal vector");

    }

    /**
     * Test method for {@link Plane#getNormal(Point)}.
     * validate that the method returns the correct normal vector for points on the plane.
     */
    @Test
    void testGetNormal() {

        Plane plane = new Plane(P2, V1);

        // ============ Equivalence Partitions Tests ==============

        // TC01: A point that is not the reference
        assertEquals(1, plane.getNormal(new Point(1, 5, -4)).length(), DELTA,
                "getNormal() wrong result for a point on the plane (not reference point)");

        //  check corect normal
        assertThrows(IllegalArgumentException.class, () -> plane.getNormal(new Point(1, 5, -4)).crossProduct(V1),
                "ERROR: getNormal() should throw exception for a point not on the plane");

        assertTrue(plane.getNormal(new Point(1, 5, -4)).dotProduct(V1) > 0,
                "ERROR: getNormal() should return a normal vector that is in the another direction as the normal vector used in the constructor");

        //== Boundary Values Tests ==================
        // TC11: A point that is not the reference
        assertEquals(1, plane.getNormal(P2).length(), DELTA,
                "getNormal() wrong result for a point on the plane (not reference point)");

        //  check corect normal
        assertThrows(IllegalArgumentException.class, () -> plane.getNormal(P2).crossProduct(V1),
                "ERROR: getNormal() should throw exception for a point not on the plane");

        assertTrue(plane.getNormal(P2).dotProduct(V1) > 0,
                "ERROR: getNormal() should return a normal vector that is in the another direction as the normal vector used in the constructor");
    }

}
