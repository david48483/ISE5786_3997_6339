package geometries;

import geometries.impl.Triangle;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for class {@link Triangle}.
 * The tests verify:
 * <ul>
 *     <li>{@link Triangle#getNormal(Point)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 *
 * @author David &amp; Yehuda
 */

public class TriangleTests {

    /**
     * Default constructor for TriangleTests.
     */
    public TriangleTests() {
    }

    /**
     * Points used in the tests
     */
    private static final Point PX = new Point(4, 0, 0);

    /**
     * Another point used in the tests
     */
    private static final Point PY = new Point(0, 4, 0);

    //  create a simple triangle in the XY plane
    private static final Triangle triangle = new Triangle(PX, PY, Point.ZERO);

    /**
     * Test method for {@link Triangle#getNormal(Point)}.
     * check that the normal vector is correct for points on the triangle.
     */
    @Test
    void testGetNormal() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: a regular check for a point on the triangle
        assertEquals(Vector.AXIS_Z, triangle.getNormal(new Point(1, 1, 0)),
                "ERROR: Triangle getNormal() returned wrong normal for a point inside the triangle");
    }

    /**
     * Test method for {@link Triangle#Triangle(Point, Point, Point)}.
     *
     */
    @Test
    void findIntersections() {
        // ============ Equivalence Partitions Tests ==============

        // **** Group 1: ray intersecting the triangle
        // EP01:  ray that intersects the triangle, 1 point
        Point p210 = new Point(2, 1, 0);
        Ray ray1 = new Ray(new Point(2, 1, -1), Vector.AXIS_Z);
        assertEquals(List.of(p210), triangle.findIntersections(ray1),
                "ERROR: Triangle findIntersections() returned wrong intersection point for a ray that intersects the triangle");

        //  **** Group 2: ray intersecting the plane of the triangle but not intersecting the triangle, opposite edges
        // EP02: ray opposite edge PY-PX, 0 point
        Ray ray2 = new Ray(new Point(5, 5, -1), Vector.AXIS_Z);
        assertEquals(List.of(), triangle.findIntersections(ray2),
                "ERROR: Triangle findIntersections() returned intersection points for a ray that misses the triangle");

        //EP03 ray opposite edge Point.ZERO-PX, 0 point
        Ray ray6 = new Ray(new Point(5, -2, -1), Vector.AXIS_Z);
        assertEquals(List.of(), triangle.findIntersections(ray6),
                "ERROR: Triangle findIntersections() returned intersection points for a ray that misses the triangle");

        //  EP04 ray opposite edge Point.ZERO-PY, 0 point
        Ray ray7 = new Ray(new Point(-2, 5, -1), Vector.AXIS_Z);
        assertEquals(List.of(), triangle.findIntersections(ray7),
                "ERROR: Triangle findIntersections() returned intersection points for a ray that misses the triangle");

        // **** Group 3: ray intersecting the plane of the triangle but not intersecting the triangle, opposite vertices
        //  EP05 ray opposite vertex PX, 0 point
        Ray ray3 = new Ray(new Point(6, -1, -1), Vector.AXIS_Z);
        assertEquals(List.of(), triangle.findIntersections(ray3),
                "ERROR: Triangle findIntersections() returned intersection points for a ray that misses the triangle opposite vertex");

        //EP06 ray opposite vertex PY, 0 point
        Ray ray4 = new Ray(new Point(-1, 6, -1), Vector.AXIS_Z);
        assertEquals(List.of(), triangle.findIntersections(ray4),
                "ERROR: Triangle findIntersections() returned intersection points for a ray that misses the triangle opposite vertex");

        //  EP07 ray opposite vertex Point.ZERO, 0 point
        Ray ray5 = new Ray(new Point(-1, -1, -1), Vector.AXIS_Z);
        assertEquals(List.of(), triangle.findIntersections(ray5),
                "ERROR: Triangle findIntersections() returned intersection points for a ray that misses the triangle opposite vertex");

        //EP08 ray parallel to the triangle, 0 point [check like plane]
        Ray ray17 = new Ray(new Point(1, 1, -1), Vector.AXIS_X);
        assertEquals(List.of(), triangle.findIntersections(ray17),
                "ERROR: Triangle findIntersections() returned intersection points for a ray that is parallel to the triangle");

        // =========== Boundary Values Tests ==============
        //  **** Group 1: ray not intersecting the triangle, on the edges
        //BV11 ray on edge PX-PY, 0 point
        Ray ray8 = new Ray(new Point(2, 2, -1), Vector.AXIS_Z);
        assertEquals(List.of(), triangle.findIntersections(ray8),
                "ERROR: Triangle findIntersections() returned intersection points for a ray that misses the triangle on the edge");

        //BV12 ray on edge Point.ZERO -PY, 0 point
        Ray ray9 = new Ray(new Point(0, 2, -1), Vector.AXIS_Z);
        assertEquals(List.of(), triangle.findIntersections(ray9),
                "ERROR: Triangle findIntersections() returned intersection points for a ray that misses the triangle on the edge");

        //BV13 ray on edge Point.ZERO-PX, 0 point
        Ray ray10 = new Ray(new Point(2, 0, -1), Vector.AXIS_Z);
        assertEquals(List.of(), triangle.findIntersections(ray10),
                "ERROR: Triangle findIntersections() returned intersection points for a ray that misses the triangle on the edge");

        //****    Group 2: ray intersecting the triangle on the vertices
        //B V21 ray on vertex PX, 0 point
        Ray ray11 = new Ray(new Point(4, 0, -1), Vector.AXIS_Z);
        assertEquals(List.of(), triangle.findIntersections(ray11),
                "ERROR: Triangle findIntersections() returned intersection points for a ray that misses the triangle on the vertex");
        //BV22 ray on vertex PY, 0 point
        Ray ray12 = new Ray(new Point(0, 4, -1), Vector.AXIS_Z);
        assertEquals(List.of(), triangle.findIntersections(ray12),
                "ERROR: Triangle findIntersections() returned intersection points for a ray that misses the triangle on the vertex");
        //BV23 ray on vertex Point.ZERO, 0 point
        Ray ray13 = new Ray(new Point(0, 0, -1), Vector.AXIS_Z);
        assertEquals(List.of(), triangle.findIntersections(ray13),
                "ERROR: Triangle findIntersections() returned intersection points for a ray that misses the triangle on the vertex");

        //****    Group 3: ray intersecting the triangle on the edge's continuation
        //BV31 ray on edge's continuation of PX-PY, 0 point
        Ray ray14 = new Ray(new Point(5, -1, -1), Vector.AXIS_Z);
        assertEquals(List.of(), triangle.findIntersections(ray14),
                "ERROR: Triangle findIntersections() returned intersection points for a ray that misses the triangle on the edge's continuation");
        //BV32 ray on edge's continuation of Point.ZERO -PY , 0 point
        Ray ray15 = new Ray(new Point(0, 5, -1), Vector.AXIS_Z);
        assertEquals(List.of(), triangle.findIntersections(ray15),
                "ERROR: Triangle findIntersections() returned intersection points for a ray that misses the triangle on the edge's continuation");
        //BV33 ray on edge's continuation of Point.ZERO-PX, 0 point
        Ray ray16 = new Ray(new Point(5, 0, -1), Vector.AXIS_Z);
        assertEquals(List.of(), triangle.findIntersections(ray16),
                "ERROR: Triangle findIntersections() returned intersection points for a ray that misses the triangle on the edge's continuation");

    }
}