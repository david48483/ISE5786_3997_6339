package geometries;

import geometries.impl.Triangle;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

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
 * @author David & Yheuda
 */

public class TriangleTests {

    /**
     * Test method for {@link Triangle#getNormal(Point)}.
     * check that the normal vector is correct for points on the triangle.
     */

    @Test
    void testGetNormal() {
        //  create a simple triangle in the XY plane
        Triangle triangle = new Triangle(new Point(4, 0, 0), new Point(0, 4, 0), Point.ZERO);

        Vector expectedNormal = new Vector(0, 0, 1);

        // ============ Equivalence Partitions Tests ==============

        // TC01: a regular check for a point on the triangle
        assertEquals(expectedNormal, triangle.getNormal(new Point(1, 1, 0)),
                "ERROR: Triangle getNormal() returned wrong normal for a point inside the triangle");
    }
}