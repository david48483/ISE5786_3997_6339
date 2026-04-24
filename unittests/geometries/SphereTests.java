package geometries;

import geometries.impl.Sphere;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for class {@link Sphere}.
 * The tests verify:
 * <ul>
 * <li>{@link Sphere#getNormal(Point)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 *
 * @author David &amp; Yehuda
 */

public class SphereTests {

    /**
     * Default constructor for SphereTests.
     */
    public SphereTests() {
    }

    /**
     * Test method for {@link Sphere#getNormal(Point)}.
     * check that the normal vector is correct for points on the sphere.
     */
    @Test
    void testGetNormal() {

        // ============ Equivalence Partitions Tests ==============

        // create a simple sphere
        Sphere sphere = new Sphere(Point.ZERO, 7);

        //TC01  a regular check
        assertEquals(Vector.AXIS_Z, sphere.getNormal(new Point(0, 0, 7)),
                "ERROR: Sphere getNormal() returned wrong normal for a point on the sphere");

    }

    @Test
    void testFindIntersections(){
        //============ Equivalence Partitions Tests ==============

        Sphere sphere = new Sphere(Point.ZERO, 7);
        //TC01: Ray's line is outside the sphere (0 points)
        assertEquals(java.util.List.of(), sphere.findIntersections(new Ray(new Point(0, 8, -1), Vector.AXIS_Z)),
                "ERROR: Sphere findIntersections() wrong number");

        //TC02: Ray starts before and crosses the sphere (2 points)
        Point p1 = new Point(0, 2, Math.sqrt(45));
        Point p2 = new Point(0, 2, -Math.sqrt(45));
        assertEquals(java.util.List.of(p1, p2), sphere.findIntersections(new Ray(new Point(0, 2, -8), Vector.AXIS_Z)),
                "ERROR: Sphere findIntersections() wrong number");

        //TC03: Ray starts inside the sphere (1 point)
        assertEquals(java.util.List.of(p1), sphere.findIntersections(new Ray(new Point(0, 2, 2), Vector.AXIS_Z)),
                "ERROR: Sphere findIntersections() wrong number");

        //TC04: Ray start after the sphere (0 points)
        assertEquals(java.util.List.of(), sphere.findIntersections(new Ray(new Point(0, 2, 8), Vector.AXIS_Z)),
                "ERROR: Sphere findIntersections() wrong number");

        //============ Boundary Values Tests ==================

        //TC11: Ray starts at sphere and goes inside (1 point)
        assertEquals(java.util.List.of(p1), sphere.findIntersections(new Ray(p2, Vector.AXIS_Z)),
                "ERROR: Sphere findIntersections() wrong number");

        //TC12: Ray starts at sphere and goes outside (0 point)
        assertEquals(java.util.List.of(), sphere.findIntersections(new Ray(p1, Vector.AXIS_Z)),
                "ERROR: Sphere findIntersections() wrong number");

        //TC21: Ray Tangent to the sphere (0 point)
        assertEquals(java.util.List.of(), sphere.findIntersections(new Ray(new Point(0, 7, -8), Vector.AXIS_Z)),
                "ERROR: Sphere findIntersections() wrong number");
        //TC22: Ray Tangent to the sphere (0 point)
        assertEquals(java.util.List.of(), sphere.findIntersections(new Ray(new Point(0, 7, 0), Vector.AXIS_Z)),
                "ERROR: Sphere findIntersections() wrong number");
        //TC23: Ray Tangent to the sphere (0 point)
        assertEquals(java.util.List.of(), sphere.findIntersections(new Ray(new Point(0, 7, 8), Vector.AXIS_Z)),
                "ERROR: Sphere findIntersections() wrong number");

        Point p3 = new Point(0, 0, 7);
        //TC31: Ray start at the center of the sphere (1 point)
        assertEquals(java.util.List.of(p3), sphere.findIntersections(new Ray(Point.ZERO, Vector.AXIS_Z)),
                "ERROR: Sphere findIntersections() wrong number");

        //TC32: Ray starts out side of the spera (0 point)
        assertEquals(java.util.List.of(), sphere.findIntersections(new Ray(p3, Vector.AXIS_Z)),
                "ERROR: Sphere findIntersections() wrong number");

        Point p4 = new Point(0,0,-7);
        //TC33: Ray start of the spara (1)
        assertEquals(java.util.List.of(p3), sphere.findIntersections(new Ray(p4, Vector.AXIS_Z)),
                "ERROR: Sphere findIntersections() wrong number");

        Point p5 = new Point(0,0,8);
        //TC34: Ray start of the spara (1)
        assertEquals(java.util.List.of(), sphere.findIntersections(new Ray(p4, Vector.AXIS_Z)),
                "ERROR: Sphere findIntersections() wrong number");

        Point p6 = new Point(0,0,-8);
        //TC35: Ray start of the spara (1)
        assertEquals(java.util.List.of(p3, p4), sphere.findIntersections(new Ray(p6, Vector.AXIS_Z)),
                "ERROR: Sphere findIntersections() wrong number");

        Point p7 = new Point(0,0,2);
        //TC36: Ray start of the spara (1)
        assertEquals(java.util.List.of(p3), sphere.findIntersections(new Ray(p7, Vector.AXIS_Z)),
                "ERROR: Sphere findIntersections() wrong number");

        Point p8 = new Point(0,2,0);
        //TC37: Ray start of the spara (1)
        assertEquals(java.util.List.of(p1), sphere.findIntersections(new Ray(p8, Vector.AXIS_Y)),
                "ERROR: Sphere findIntersections() wrong number");


    }
}
