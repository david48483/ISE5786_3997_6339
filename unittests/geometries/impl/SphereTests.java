package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link Sphere}.
 * Covers {@link Sphere#getNormal(Point)} and {@link Sphere#findIntersections(Ray)}.
 * Tests follow the Equivalence Partitions (EP) and Boundary Values Analysis (BVA) methodology.
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
     * Error message used when {@link Sphere#getNormal(Point)} returns a wrong result.
     */
    private static final String ERR_GET_NORMAL =
            "ERROR: Sphere getNormal() returned wrong normal for a point on the sphere";

    /**
     * Error message used when {@link Sphere#findIntersections(Ray)} returns a wrong result.
     */
    private static final String ERR_FIND_INTERSECTIONS =
            "ERROR: Sphere findIntersections() wrong number";

    /**
     * Front intersection of a ray along Z through y=2 with the unit-test sphere (radius 7).
     */
    private static final Point P1 = new Point(0, 2, Math.sqrt(45));

    /**
     * Back intersection of a ray along Z through y=2 with the unit-test sphere (radius 7).
     */
    private static final Point P2 = new Point(0, 2, -Math.sqrt(45));

    /**
     * North pole of the unit-test sphere — on-axis surface point at z = 7.
     */
    private static final Point P3 = new Point(0, 0, 7);

    /**
     * South pole of the unit-test sphere — on-axis surface point at z = −7.
     */
    private static final Point P4 = new Point(0, 0, -7);

    /**
     * Tests {@link Sphere#getNormal(Point)}.
     * Verifies that the normal at a point on the surface is the correct unit vector
     * pointing away from the center.
     */
    @Test
    void testGetNormal() {

        // ============ Equivalence Partitions Tests ==============

        Sphere sphere = new Sphere(Point.ZERO, 7);

        // TC01: Normal at the north pole of the sphere must equal +Z
        assertEquals(Vector.AXIS_Z, sphere.getNormal(new Point(0, 0, 7)),
                ERR_GET_NORMAL);

    }

    /**
     * Tests {@link Sphere#findIntersections(Ray)}.
     * Covers rays that miss the sphere, cross it at two points, start inside it,
     * start after it, start on its surface, are tangent to it, and start at its center.
     */
    @Test
    void testFindIntersections() {
        Sphere sphere = new Sphere(Point.ZERO, 7);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray's line is entirely outside the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(0, 8, -1), Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC02: Ray starts before and crosses the sphere (2 points)
        assertEquals(List.of(P2, P1), sphere.findIntersections(new Ray(new Point(0, 2, -8), Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC03: Ray starts inside the sphere (1 point)
        assertEquals(List.of(P1), sphere.findIntersections(new Ray(new Point(0, 2, 2), Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC04: Ray starts after the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(0, 2, 8), Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // ============ Boundary Values Tests ==================

        // TC11: Ray starts on the sphere surface and goes inside (1 point)
        assertEquals(List.of(P1), sphere.findIntersections(new Ray(P2, Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);
        
        // TC12: Ray starts on the sphere surface and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(P1, Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC21: Ray is tangent to the sphere - starts before tangent point (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(0, 7, -8), Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC22: Ray is tangent to the sphere - starts at tangent point (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(0, 7, 0), Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC23: Ray is tangent to the sphere - starts after tangent point (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(0, 7, 8), Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC31: Ray starts at the center of the sphere (1 point)
        assertEquals(List.of(P3), sphere.findIntersections(new Ray(Point.ZERO, Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC32: Ray starts on the north pole going outward (0 points)
        assertNull(sphere.findIntersections(new Ray(P3, Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC33: Ray starts on the south pole going inward (1 point)
        assertEquals(List.of(P3), sphere.findIntersections(new Ray(P4, Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC34: Ray starts beyond the north pole going outward (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(0, 0, 8), Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC35: Ray starts outside before south pole and crosses through (2 points)
        assertEquals(List.of(P4, P3), sphere.findIntersections(new Ray(new Point(0, 0, -8), Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC36: Ray starts inside on Z axis going toward north pole (1 point)
        assertEquals(List.of(P3), sphere.findIntersections(new Ray(new Point(0, 0, 2), Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC37: Ray starts inside on the Y=2 plane going in +Y direction (1 point)
        assertEquals(List.of(new Point(0, 7, 0)), sphere.findIntersections(new Ray(new Point(0, 2, 0), Vector.AXIS_Y)),
                ERR_FIND_INTERSECTIONS);

    }
}
