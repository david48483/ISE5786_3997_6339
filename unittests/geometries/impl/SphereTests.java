package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.AABB;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Sphere}.
 * Covers {@link Sphere#getNormal(Point)} and {@link Sphere#findIntersections(Ray)}.
 * Tests follow the Equivalence Partitions (EP) and Boundary Values Analysis (BVA) methodology.
 *
 * @author David  &amp; Yehuda
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

    /**
     * Tests {@link Sphere#calcIntersections(Ray)}.
     * Covers rays that miss the sphere, cross it at two points, start inside it,
     * start after it, start on its surface, are tangent to it, and start at its center.
     */
    @Test
    void testCalcIntersections() {
        Sphere sphere = new Sphere(Point.ZERO, 7);
        Ray testRay = new Ray(new Point(0, 2, -8), Vector.AXIS_Z);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray's line is entirely outside the sphere (0 points)
        assertNull(sphere.calcIntersections(new Ray(new Point(0, 8, -1), Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC02: Ray starts before and crosses the sphere (2 points)
        var resultTC02 = sphere.calcIntersections(testRay);
        assertEquals(2, resultTC02.size(), ERR_FIND_INTERSECTIONS);
        assertSame(sphere, resultTC02.get(0).geometry, ERR_FIND_INTERSECTIONS);
        assertEquals(P2, resultTC02.get(0).point, ERR_FIND_INTERSECTIONS);
        assertSame(sphere, resultTC02.get(1).geometry, ERR_FIND_INTERSECTIONS);
        assertEquals(P1, resultTC02.get(1).point, ERR_FIND_INTERSECTIONS);

        // TC03: Ray starts inside the sphere (1 point)
        var resultTC03 = sphere.calcIntersections(new Ray(new Point(0, 2, 2), Vector.AXIS_Z));
        assertEquals(1, resultTC03.size(), ERR_FIND_INTERSECTIONS);
        assertSame(sphere, resultTC03.getFirst().geometry, ERR_FIND_INTERSECTIONS);
        assertEquals(P1, resultTC03.getFirst().point, ERR_FIND_INTERSECTIONS);

        // TC04: Ray starts after the sphere (0 points)
        assertNull(sphere.calcIntersections(new Ray(new Point(0, 2, 8), Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC05: Ray starts before and crosses the sphere, but maxDistance is less than the distance to the first intersection (0 points)
        assertNull(sphere.calcIntersections(testRay, 0.5),
                ERR_FIND_INTERSECTIONS);

        // TC06: Ray starts before and crosses the sphere, but maxDistance is less than the distance to the second intersection (1 point)
        assertEquals(1, sphere.calcIntersections(testRay, 5).size(), ERR_FIND_INTERSECTIONS);

        // TC07: Ray starts before and crosses the sphere, but maxDistance is greater than the distance to the second intersection (2 points)
        assertEquals(2, sphere.calcIntersections(testRay, 16).size(), ERR_FIND_INTERSECTIONS);

        // TC08: Ray starts inside the sphere but maxDistance shorter than distance to intersection (0 points)
        assertNull(sphere.calcIntersections(new Ray(new Point(0, 2, 2), Vector.AXIS_Z), 2),
                ERR_FIND_INTERSECTIONS);

        // TC09: Ray starts inside the sphere and maxDistance includes the exit point (1 point)
        assertEquals(1, sphere.calcIntersections(new Ray(new Point(0, 2, 2), Vector.AXIS_Z), 10).size(),
                ERR_FIND_INTERSECTIONS);

        // TC10: Ray starts after the sphere; even with maxDistance=10 there are 0 intersections (0 points)
        assertNull(sphere.calcIntersections(new Ray(new Point(0, 2, 8), Vector.AXIS_Z), 10),
                ERR_FIND_INTERSECTIONS);

        // ============ Boundary Values Tests ==================

        // TC11: Ray starts on the sphere surface and goes inside (1 point)
        var resultTC11 = sphere.calcIntersections(new Ray(P2, Vector.AXIS_Z));
        assertEquals(1, resultTC11.size(), ERR_FIND_INTERSECTIONS);
        assertSame(sphere, resultTC11.getFirst().geometry, ERR_FIND_INTERSECTIONS);
        assertEquals(P1, resultTC11.getFirst().point, ERR_FIND_INTERSECTIONS);

        // TC12: Ray starts on the sphere surface and goes outside (0 points)
        assertNull(sphere.calcIntersections(new Ray(P1, Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC21: Ray is tangent to the sphere - starts before tangent point (0 points)
        assertNull(sphere.calcIntersections(new Ray(new Point(0, 7, -8), Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC22: Ray is tangent to the sphere - starts at tangent point (0 points)
        assertNull(sphere.calcIntersections(new Ray(new Point(0, 7, 0), Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC23: Ray is tangent to the sphere - starts after tangent point (0 points)
        assertNull(sphere.calcIntersections(new Ray(new Point(0, 7, 8), Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC31: Ray starts at the center of the sphere (1 point)
        var resultTC31 = sphere.calcIntersections(new Ray(Point.ZERO, Vector.AXIS_Z));
        assertEquals(1, resultTC31.size(), ERR_FIND_INTERSECTIONS);
        assertSame(sphere, resultTC31.getFirst().geometry, ERR_FIND_INTERSECTIONS);
        assertEquals(P3, resultTC31.getFirst().point, ERR_FIND_INTERSECTIONS);

        // TC32: Ray starts on the north pole going outward (0 points)
        assertNull(sphere.calcIntersections(new Ray(P3, Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC33: Ray starts on the south pole going inward (1 point)
        var resultTC33 = sphere.calcIntersections(new Ray(P4, Vector.AXIS_Z));
        assertEquals(1, resultTC33.size(), ERR_FIND_INTERSECTIONS);
        assertSame(sphere, resultTC33.getFirst().geometry, ERR_FIND_INTERSECTIONS);
        assertEquals(P3, resultTC33.getFirst().point, ERR_FIND_INTERSECTIONS);

        // TC34: Ray starts beyond the north pole going outward (0 points)
        assertNull(sphere.calcIntersections(new Ray(new Point(0, 0, 8), Vector.AXIS_Z)),
                ERR_FIND_INTERSECTIONS);

        // TC35: Ray starts outside before south pole and crosses through (2 points)
        var resultTC35 = sphere.calcIntersections(new Ray(new Point(0, 0, -8), Vector.AXIS_Z));
        assertEquals(2, resultTC35.size(), ERR_FIND_INTERSECTIONS);
        assertSame(sphere, resultTC35.get(0).geometry, ERR_FIND_INTERSECTIONS);
        assertEquals(P4, resultTC35.get(0).point, ERR_FIND_INTERSECTIONS);
        assertSame(sphere, resultTC35.get(1).geometry, ERR_FIND_INTERSECTIONS);
        assertEquals(P3, resultTC35.get(1).point, ERR_FIND_INTERSECTIONS);

        // TC36: Ray starts inside on Z axis going toward north pole (1 point)
        var resultTC36 = sphere.calcIntersections(new Ray(new Point(0, 0, 2), Vector.AXIS_Z));
        assertEquals(1, resultTC36.size(), ERR_FIND_INTERSECTIONS);
        assertSame(sphere, resultTC36.getFirst().geometry, ERR_FIND_INTERSECTIONS);
        assertEquals(P3, resultTC36.getFirst().point, ERR_FIND_INTERSECTIONS);

        // TC37: Ray starts inside on the Y=2 plane going in +Y direction (1 point)
        var resultTC37 = sphere.calcIntersections(new Ray(new Point(0, 2, 0), Vector.AXIS_Y));
        assertEquals(1, resultTC37.size(), ERR_FIND_INTERSECTIONS);
        assertSame(sphere, resultTC37.getFirst().geometry, ERR_FIND_INTERSECTIONS);
        assertEquals(new Point(0, 7, 0), resultTC37.getFirst().point, ERR_FIND_INTERSECTIONS);
    }

    /**
     * Tests the bounding box creation and intersection logic for {@link Sphere}.
     * Verifies the AABB boundaries and tests ray intersections against the AABB
     * (including rays starting outside, inside, on the boundaries, and distance limits).
     */
    @Test
    void testBoundingBox() {
        Sphere sphere = new Sphere(Point.ZERO, 7);
        AABB aabb = sphere.getBoundingBox(); // This will trigger setBoundingBoxHelper()

        // ============ Equivalence Partitions Tests (Box Creation) ==============
        // EP01: Verify the bounding box dimensions (Min and Max points)
        assertEquals(new Point(-7, -7, -7), aabb.getMin(),
                "ERROR: Bounding box min point is incorrect");
        assertEquals(new Point(7, 7, 7), aabb.getMax(),
                "ERROR: Bounding box max point is incorrect");

        // -----------------------------------------------------------------------
        // Test Ray-AABB Intersections
        // -----------------------------------------------------------------------
        double maxDistance = Double.POSITIVE_INFINITY;

        // ============ Equivalence Partitions Tests (Intersections) ==============
        // EP11: Ray starts outside and intersects the AABB
        assertTrue(aabb.intersects(new Ray(new Point(0, 0, -10), Vector.AXIS_Z), maxDistance),
                "ERROR: Ray intersecting AABB from outside should return true");

        // EP12: Ray misses the AABB entirely
        assertFalse(aabb.intersects(new Ray(new Point(0, 10, -10), Vector.AXIS_Z), maxDistance),
                "ERROR: Ray missing the AABB should return false");

        // EP13: Ray intersects the AABB, but intersection is beyond maxDistance
        assertFalse(aabb.intersects(new Ray(new Point(0, 0, -10), Vector.AXIS_Z), 2.0),
                "ERROR: Ray intersecting AABB beyond maxDistance should return false");

        // ============ Boundary Values Tests (Intersections) ==================

        // BVA01: Ray starts strictly INSIDE the AABB
        assertTrue(aabb.intersects(new Ray(Point.ZERO, Vector.AXIS_Z), maxDistance),
                "ERROR: Ray starting inside the AABB should return true");

        // BVA02: Ray starts ON the AABB boundary (z = -7) pointing INWARDS
        assertTrue(aabb.intersects(new Ray(new Point(0, 0, -7), Vector.AXIS_Z), maxDistance),
                "ERROR: Ray starting on the AABB boundary pointing inwards should return true");

        // BVA03: Ray starts ON the AABB boundary (z = 7) pointing OUTWARDS
        // (Since it starts on the boundary, t=0 is a valid intersection)
        assertTrue(aabb.intersects(new Ray(new Point(0, 0, 7), Vector.AXIS_Z), maxDistance),
                "ERROR: Ray starting on the AABB boundary pointing outwards should return true (intersects at t=0)");

        // BVA04: Ray runs parallel to a bounding plane (Z-axis), sliding exactly on the edge
        assertTrue(aabb.intersects(new Ray(new Point(7, 7, -10), Vector.AXIS_Z), maxDistance),
                "ERROR: Ray sliding perfectly along the AABB edge should return true");

        // BVA05: Ray runs parallel to a bounding plane, strictly missing it (just outside)
        assertFalse(aabb.intersects(new Ray(new Point(8, 7, -10), Vector.AXIS_Z), maxDistance),
                "ERROR: Ray parallel to AABB but slightly outside should return false");

        // BVA06: Ray starts inside, but maxDistance is exactly 0
        assertTrue(aabb.intersects(new Ray(Point.ZERO, Vector.AXIS_Z), 0.0),
                "ERROR: Ray starting inside with maxDistance 0 should intersect at t=0");
    }

    /**
     * Tests the ray-AABB intersection logic specifically focusing on the maxDistance parameter.
     * Verifies that intersections are correctly identified or rejected based on finite distance limits.
     */
    @Test
    void testAABBDistance() {
        // Create a standard AABB from (-7, -7, -7) to (7, 7, 7)
        AABB aabb = new AABB(new Point(-7, -7, -7), new Point(7, 7, 7));

        // Ray starts at z = -10, pointing towards +Z.
        // It enters the box at distance t = 3 and exits at t = 17.
        Ray rayOutside = new Ray(new Point(0, 0, -10), Vector.AXIS_Z);

        // ============ Equivalence Partitions Tests ==============

        // EP01: maxDistance is completely before the box (e.g., 2.0 < 3.0)
        assertFalse(aabb.intersects(rayOutside, 2.0),
                "ERROR: Ray should not intersect if maxDistance is shorter than the distance to the box");

        // EP02: maxDistance is inside the box (e.g., 10.0 is between 3.0 and 17.0)
        assertTrue(aabb.intersects(rayOutside, 10.0),
                "ERROR: Ray should intersect if maxDistance reaches inside the box");

        // EP03: maxDistance is beyond the box (e.g., 20.0 > 17.0)
        assertTrue(aabb.intersects(rayOutside, 20.0),
                "ERROR: Ray should intersect if maxDistance is beyond the box limits");

        // ============ Boundary Values Tests ==================

        // BVA01: maxDistance is EXACTLY the distance to the entry point (t = 3.0)
        assertTrue(aabb.intersects(rayOutside, 3.0),
                "ERROR: Ray should intersect if maxDistance is exactly at the entry boundary");

        // BVA02: Ray starts strictly inside the box, limited by a very short maxDistance
        Ray rayInside = new Ray(Point.ZERO, Vector.AXIS_Z);
        assertTrue(aabb.intersects(rayInside, 0.5),
                "ERROR: Ray starting inside should intersect even with a very short maxDistance");

        // BVA03: Ray starts inside the box, maxDistance is exactly 0.0
        assertTrue(aabb.intersects(rayInside, 0.0),
                "ERROR: Ray starting inside with maxDistance 0.0 should intersect at t=0");

        // BVA04: Ray starts exactly ON the boundary, points outwards, maxDistance is exactly 0.0
        Ray rayOnBoundary = new Ray(new Point(0, 0, 7), Vector.AXIS_Z);
        assertTrue(aabb.intersects(rayOnBoundary, 0.0),
                "ERROR: Ray starting on boundary pointing outwards with maxDistance 0.0 should intersect");
    }
}
