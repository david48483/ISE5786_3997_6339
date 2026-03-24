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
 * @author David & Yheuda
 */

public class PlaneTests {

    //  Points used in the tests
    private static final Point P2 = new Point(1, 0, 0);

    //  Another point used in the tests
    private static final Point P3 = new Point(0, 1, 0);

    //  A vector used in the tests
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

        //  TC12 First and third points are the same.
        assertThrows(IllegalArgumentException.class, () -> new Plane(P2, P3, P2),
                "ERROR: Plane constructor should throw exception when first and third points are the same");

        //  TC13 Second and third points are the same.
        assertThrows(IllegalArgumentException.class, () -> new Plane(P3, P2, P2),
                "ERROR: Plane constructor should throw exception when second and third points are the same");

        //  TC14 All three points are the same collinear.
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

        Plane plane = new Plane(P2, V1);
        Vector expectedNormal = new Vector(3 / Math.sqrt(50), 4 / Math.sqrt(50), 5 / Math.sqrt(50));

        //  ============ Equivalence Partitions Tests ==============

        //  TC01 check constructor.
        assertEquals(expectedNormal, plane.getNormal(P2),
                "ERROR: Plane constructor failed to create the expected plane with point and normal vector");

    }

    /**
     * Test method for {@link Plane#getNormal(Point)}.
     * validate that the method returns the correct normal vector for points on the plane.
     */
    @Test
    void testGetNormal() {

        Plane plane = new Plane(Point.ZERO, P2, P3);

        Vector expectedNormal = new Vector(0, 0, 1);

        // ============ Equivalence Partitions Tests ==============

        // TC01: A point that is not the reference
        assertEquals(expectedNormal, plane.getNormal(P2),
                "getNormal() wrong result for a point on the plane (not reference point)");

        // =============== Boundary Values Tests ==================

        // TC10: the reference point
        assertEquals(expectedNormal, plane.getNormal(Point.ZERO),
                "getNormal() wrong result for the reference point of the plane");
    }

}
