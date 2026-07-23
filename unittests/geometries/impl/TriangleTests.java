package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.AABB;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link Triangle}.
 * The tests verify:
 * <ul>
 *     <li>{@link Triangle#getNormal(Point)}</li>
 *     <li>{@link Triangle#findIntersections(Ray)}</li>
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
     * A small delta for comparing floating-point numbers
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for {@link Triangle#getNormal(Point)} tests.
     */
    private static final String ERR_GET_NORMAL =
            "ERROR: Triangle getNormal() returned wrong normal";

    /**
     * Error message for {@link Triangle#findIntersections(Ray)} tests.
     */
    private static final String ERR_FIND_INTERSECTIONS =
            "ERROR: Triangle findIntersections() returned wrong result";

    /**
     * Vertex of the triangle on the positive X axis.
     */
    private static final Point PX = new Point(4, 0, 0);

    /**
     * Vertex of the triangle on the positive Y axis.
     */
    private static final Point PY = new Point(0, 4, 0);

    /**
     * Triangle in the XY plane with vertices at (4,0,0), (0,4,0) and the origin.
     */
    private static final Triangle TRIANGLE = new Triangle(PX, PY, Point.ZERO);

    /**
     * A point strictly inside the triangle, used for getNormal.
     */
    private static final Point P_INSIDE = new Point(1, 1, 0);

    /**
     * Expected intersection point for inside-hit cases.
     */
    private static final Point P_HIT = new Point(2, 1, 0);

    /**
     * A point in the triangle plane, not on a vertex, and inside the triangle.
     */
    private static final Point P_IN_PLANE_INSIDE = new Point(2, 1, 0);

    /**
     * Ray from (2,1,-1) +Z - hits strictly inside the triangle.
     */
    private static final Ray RAY_HIT = new Ray(new Point(2, 1, -1), Vector.AXIS_Z);

    /**
     * Ray from (5,5,-1) +Z - misses opposite edge PY-PX.
     */
    private static final Ray RAY_OPP_EDGE_PY_PX = new Ray(new Point(5, 5, -1), Vector.AXIS_Z);

    /**
     * Ray from (5,-2,-1) +Z - misses opposite edge ZERO-PX.
     */
    private static final Ray RAY_OPP_EDGE_ZERO_PX = new Ray(new Point(5, -2, -1), Vector.AXIS_Z);

    /**
     * Ray from (-2,5,-1) +Z - misses opposite edge ZERO-PY.
     */
    private static final Ray RAY_OPP_EDGE_ZERO_PY = new Ray(new Point(-2, 5, -1), Vector.AXIS_Z);

    /**
     * Ray from (6,-1,-1) +Z - misses opposite vertex PX.
     */
    private static final Ray RAY_OPP_VERTEX_PX = new Ray(new Point(6, -1, -1), Vector.AXIS_Z);

    /**
     * Ray from (-1,6,-1) +Z - misses opposite vertex PY.
     */
    private static final Ray RAY_OPP_VERTEX_PY = new Ray(new Point(-1, 6, -1), Vector.AXIS_Z);

    /**
     * Ray from (-1,-1,-1) +Z - misses opposite vertex ZERO.
     */
    private static final Ray RAY_OPP_VERTEX_ZERO = new Ray(new Point(-1, -1, -1), Vector.AXIS_Z);

    /**
     * Ray from (1,1,-1) +X - parallel to the triangle plane.
     */
    private static final Ray RAY_PARALLEL = new Ray(new Point(1, 1, -1), Vector.AXIS_X);

    // ---- BVA rays — edges ----

    /**
     * BVA11: Ray from (2,2,−1) +Z — hits edge PX–PY.
     */
    private static final Ray RAY_ON_EDGE_PX_PY = new Ray(new Point(2, 2, -1), Vector.AXIS_Z);

    /**
     * BVA12: Ray from (0,2,−1) +Z — hits edge ZERO–PY.
     */
    private static final Ray RAY_ON_EDGE_ZERO_PY = new Ray(new Point(0, 2, -1), Vector.AXIS_Z);

    /**
     * BVA13: Ray from (2,0,−1) +Z — hits edge ZERO–PX.
     */
    private static final Ray RAY_ON_EDGE_ZERO_PX = new Ray(new Point(2, 0, -1), Vector.AXIS_Z);

    // ---- BVA rays — vertices ----

    /**
     * BVA21: Ray from (4,0,−1) +Z — hits vertex PX.
     */
    private static final Ray RAY_ON_VERTEX_PX = new Ray(new Point(4, 0, -1), Vector.AXIS_Z);

    /**
     * BVA22: Ray from (0,4,−1) +Z — hits vertex PY.
     */
    private static final Ray RAY_ON_VERTEX_PY = new Ray(new Point(0, 4, -1), Vector.AXIS_Z);

    /**
     * BVA23: Ray from (0,0,−1) +Z — hits vertex ZERO.
     */
    private static final Ray RAY_ON_VERTEX_ZERO = new Ray(new Point(0, 0, -1), Vector.AXIS_Z);

    // ---- BVA rays — edge continuations ----

    /**
     * BVA31: Ray from (5,−1,−1) +Z — on continuation of edge PX–PY.
     */
    private static final Ray RAY_CONT_PX_PY = new Ray(new Point(5, -1, -1), Vector.AXIS_Z);

    /**
     * BVA32: Ray from (0,5,−1) +Z — on continuation of edge ZERO–PY.
     */
    private static final Ray RAY_CONT_ZERO_PY = new Ray(new Point(0, 5, -1), Vector.AXIS_Z);

    /**
     * BVA33: Ray from (5,0,−1) +Z — on continuation of edge ZERO–PX.
     */
    private static final Ray RAY_CONT_ZERO_PX = new Ray(new Point(5, 0, -1), Vector.AXIS_Z);

    // ---- Tests ----

    /**
     * Test method for {@link Triangle#getNormal(Point)}.
     * Verifies that the normal vector is correct for a point on the triangle.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        Vector normal = TRIANGLE.getNormal(P_INSIDE);

        // EP01: Point strictly inside the triangle - normal must equal +Z
        assertEquals(1, normal.length(), DELTA,
                ERR_GET_NORMAL);

        //  Normal points in the correct direction (same direction as expected +Z)
        assertEquals(1, Math.abs(normal.dotProduct(Vector.AXIS_Z)), DELTA,
                ERR_GET_NORMAL);
    }

    /**
     * Test method for {@link Triangle#findIntersections(Ray)}.
     * Includes all inherited plane cases and triangle-specific cases.
     */
    @Test
    void testFindIntersections() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray intersects the plane and the triangle (1 point)
        assertEquals(List.of(P_HIT), TRIANGLE.findIntersections(RAY_HIT), ERR_FIND_INTERSECTIONS);

        // EP02: Ray line intersects the plane, but the ray points away (0 points)
        assertNull(TRIANGLE.findIntersections(new Ray(new Point(2, 1, 1), Vector.AXIS_Z)), ERR_FIND_INTERSECTIONS);

        // EP03: Ray hits the triangle plane but misses - opposite edge PY-PX
        assertNull(TRIANGLE.findIntersections(RAY_OPP_EDGE_PY_PX), ERR_FIND_INTERSECTIONS);

        // EP04: Ray hits the triangle plane but misses - opposite edge ZERO-PX
        assertNull(TRIANGLE.findIntersections(RAY_OPP_EDGE_ZERO_PX), ERR_FIND_INTERSECTIONS);

        // EP05: Ray hits the triangle plane but misses - opposite edge ZERO-PY
        assertNull(TRIANGLE.findIntersections(RAY_OPP_EDGE_ZERO_PY), ERR_FIND_INTERSECTIONS);

        // EP06: Ray hits the triangle plane but misses - opposite vertex PX
        assertNull(TRIANGLE.findIntersections(RAY_OPP_VERTEX_PX), ERR_FIND_INTERSECTIONS);

        // EP07: Ray hits the triangle plane but misses - opposite vertex PY
        assertNull(TRIANGLE.findIntersections(RAY_OPP_VERTEX_PY), ERR_FIND_INTERSECTIONS);

        // EP08: Ray hits the triangle plane but misses - opposite vertex ZERO
        assertNull(TRIANGLE.findIntersections(RAY_OPP_VERTEX_ZERO), ERR_FIND_INTERSECTIONS);

        // ============ Boundary Values Tests ==============

        // BVA01: Ray is parallel and included in the plane (0 points)
        assertNull(TRIANGLE.findIntersections(new Ray(P_IN_PLANE_INSIDE, Vector.AXIS_X)), ERR_FIND_INTERSECTIONS);

        // BVA02: Ray is parallel and not included in the plane (0 points)
        assertNull(TRIANGLE.findIntersections(new Ray(new Point(2, 1, 1), Vector.AXIS_X)), ERR_FIND_INTERSECTIONS);

        // BVA03: Ray is orthogonal to the plane and starts before the plane (1 point)
        assertEquals(List.of(P_HIT), TRIANGLE.findIntersections(RAY_HIT), ERR_FIND_INTERSECTIONS);

        // BVA04: Ray is orthogonal to the plane and starts in the plane (0 points)
        assertNull(TRIANGLE.findIntersections(new Ray(P_IN_PLANE_INSIDE, Vector.AXIS_Z)), ERR_FIND_INTERSECTIONS);

        // BVA05: Ray is orthogonal to the plane and starts after the plane (0 points)
        assertNull(TRIANGLE.findIntersections(new Ray(new Point(2, 1, 1), Vector.AXIS_Z)), ERR_FIND_INTERSECTIONS);

        // BVA06: Ray starts in the plane (not reference point), not parallel and not orthogonal (0 points)
        assertNull(TRIANGLE.findIntersections(new Ray(P_IN_PLANE_INSIDE, new Vector(1, 1, 1))), ERR_FIND_INTERSECTIONS);

        // BVA07: Ray on edge PX-PY (0 points)
        assertNull(TRIANGLE.findIntersections(RAY_ON_EDGE_PX_PY), ERR_FIND_INTERSECTIONS);

        // BVA08: Ray on edge ZERO-PY (0 points)
        assertNull(TRIANGLE.findIntersections(RAY_ON_EDGE_ZERO_PY), ERR_FIND_INTERSECTIONS);

        // BVA09: Ray on edge ZERO-PX (0 points)
        assertNull(TRIANGLE.findIntersections(RAY_ON_EDGE_ZERO_PX), ERR_FIND_INTERSECTIONS);

        // BVA10: Ray on vertex PX (0 points)
        assertNull(TRIANGLE.findIntersections(RAY_ON_VERTEX_PX), ERR_FIND_INTERSECTIONS);

        // BVA11: Ray on vertex PY (0 points)
        assertNull(TRIANGLE.findIntersections(RAY_ON_VERTEX_PY), ERR_FIND_INTERSECTIONS);

        // BVA12: Ray on vertex ZERO (0 points)
        assertNull(TRIANGLE.findIntersections(RAY_ON_VERTEX_ZERO), ERR_FIND_INTERSECTIONS);

        // BVA13: Ray on continuation of edge PX-PY (0 points)
        assertNull(TRIANGLE.findIntersections(RAY_CONT_PX_PY), ERR_FIND_INTERSECTIONS);

        // BVA14: Ray on continuation of edge ZERO-PY (0 points)
        assertNull(TRIANGLE.findIntersections(RAY_CONT_ZERO_PY), ERR_FIND_INTERSECTIONS);

        // BVA15: Ray on continuation of edge ZERO-PX (0 points)
        assertNull(TRIANGLE.findIntersections(RAY_CONT_ZERO_PX), ERR_FIND_INTERSECTIONS);

        // BVA16: Additional parallel-to-plane miss case (0 points)
        assertNull(TRIANGLE.findIntersections(RAY_PARALLEL), ERR_FIND_INTERSECTIONS);
    }

    /**
     * Test method for {@link Triangle#calcIntersections(Ray)}.
     * Includes all inherited plane cases and triangle-specific cases,
     * ensuring that the returned GeoPoint contains the correct geometry and point.
     */
    @Test
    void testCalcIntersections() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray intersects the plane and the triangle (1 point)
        var resultEP01 = TRIANGLE.calcIntersections(RAY_HIT);
        assertNotNull(resultEP01, ERR_FIND_INTERSECTIONS);
        assertEquals(1, resultEP01.size(), ERR_FIND_INTERSECTIONS);
        assertSame(TRIANGLE, resultEP01.getFirst().geometry, ERR_FIND_INTERSECTIONS);
        assertEquals(P_HIT, resultEP01.getFirst().point, ERR_FIND_INTERSECTIONS);

        // EP02: Ray line intersects the plane, but the ray points away (0 points)
        assertNull(TRIANGLE.calcIntersections(new Ray(new Point(2, 1, 1), Vector.AXIS_Z)), ERR_FIND_INTERSECTIONS);

        // EP03: Ray hits the triangle plane but misses - opposite edge PY-PX
        assertNull(TRIANGLE.calcIntersections(RAY_OPP_EDGE_PY_PX), ERR_FIND_INTERSECTIONS);

        // EP04: Ray hits the triangle plane but misses - opposite edge ZERO-PX
        assertNull(TRIANGLE.calcIntersections(RAY_OPP_EDGE_ZERO_PX), ERR_FIND_INTERSECTIONS);

        // EP05: Ray hits the triangle plane but misses - opposite edge ZERO-PY
        assertNull(TRIANGLE.calcIntersections(RAY_OPP_EDGE_ZERO_PY), ERR_FIND_INTERSECTIONS);

        // EP06: Ray hits the triangle plane but misses - opposite vertex PX
        assertNull(TRIANGLE.calcIntersections(RAY_OPP_VERTEX_PX), ERR_FIND_INTERSECTIONS);

        // EP07: Ray hits the triangle plane but misses - opposite vertex PY
        assertNull(TRIANGLE.calcIntersections(RAY_OPP_VERTEX_PY), ERR_FIND_INTERSECTIONS);

        // EP08: Ray hits the triangle plane but misses - opposite vertex ZERO
        assertNull(TRIANGLE.calcIntersections(RAY_OPP_VERTEX_ZERO), ERR_FIND_INTERSECTIONS);

        //EP09 distance of point longer than max distance
        assertNull(TRIANGLE.calcIntersections(RAY_HIT, 0.5), ERR_FIND_INTERSECTIONS);

        //EP10 distance of point shorter than max distance
        var resultEP10 = TRIANGLE.calcIntersections(RAY_HIT, 2);
        assertNotNull(resultEP10, ERR_FIND_INTERSECTIONS);
        assertEquals(1, resultEP10.size(), ERR_FIND_INTERSECTIONS);

        //EP11 ray start after the triangle
        assertNull(TRIANGLE.calcIntersections(new Ray(new Point(2, 1, 1), Vector.AXIS_Z), 2), ERR_FIND_INTERSECTIONS);

        // ============ Boundary Values Tests ==============

        // BVA01: Ray is parallel and included in the plane (0 points)
        assertNull(TRIANGLE.calcIntersections(new Ray(P_IN_PLANE_INSIDE, Vector.AXIS_X)), ERR_FIND_INTERSECTIONS);

        // BVA02: Ray is parallel and not included in the plane (0 points)
        assertNull(TRIANGLE.calcIntersections(new Ray(new Point(2, 1, 1), Vector.AXIS_X)), ERR_FIND_INTERSECTIONS);

        // BVA03: Ray is orthogonal to the plane and starts before the plane (1 point)
        var resultBVA03 = TRIANGLE.calcIntersections(RAY_HIT);
        assertNotNull(resultBVA03, ERR_FIND_INTERSECTIONS);
        assertEquals(1, resultBVA03.size(), ERR_FIND_INTERSECTIONS);
        assertSame(TRIANGLE, resultBVA03.getFirst().geometry, ERR_FIND_INTERSECTIONS);
        assertEquals(P_HIT, resultBVA03.getFirst().point, ERR_FIND_INTERSECTIONS);

        // BVA04: Ray is orthogonal to the plane and starts in the plane (0 points)
        assertNull(TRIANGLE.calcIntersections(new Ray(P_IN_PLANE_INSIDE, Vector.AXIS_Z)), ERR_FIND_INTERSECTIONS);

        // BVA05: Ray is orthogonal to the plane and starts after the plane (0 points)
        assertNull(TRIANGLE.calcIntersections(new Ray(new Point(2, 1, 1), Vector.AXIS_Z)), ERR_FIND_INTERSECTIONS);

        // BVA06: Ray starts in the plane (not reference point), not parallel and not orthogonal (0 points)
        assertNull(TRIANGLE.calcIntersections(new Ray(P_IN_PLANE_INSIDE, new Vector(1, 1, 1))), ERR_FIND_INTERSECTIONS);

        // BVA07: Ray on edge PX-PY (0 points)
        assertNull(TRIANGLE.calcIntersections(RAY_ON_EDGE_PX_PY), ERR_FIND_INTERSECTIONS);

        // BVA08: Ray on edge ZERO-PY (0 points)
        assertNull(TRIANGLE.calcIntersections(RAY_ON_EDGE_ZERO_PY), ERR_FIND_INTERSECTIONS);

        // BVA09: Ray on edge ZERO-PX (0 points)
        assertNull(TRIANGLE.calcIntersections(RAY_ON_EDGE_ZERO_PX), ERR_FIND_INTERSECTIONS);

        // BVA10: Ray on vertex PX (0 points)
        assertNull(TRIANGLE.calcIntersections(RAY_ON_VERTEX_PX), ERR_FIND_INTERSECTIONS);

        // BVA11: Ray on vertex PY (0 points)
        assertNull(TRIANGLE.calcIntersections(RAY_ON_VERTEX_PY), ERR_FIND_INTERSECTIONS);

        // BVA12: Ray on vertex ZERO (0 points)
        assertNull(TRIANGLE.calcIntersections(RAY_ON_VERTEX_ZERO), ERR_FIND_INTERSECTIONS);

        // BVA13: Ray on continuation of edge PX-PY (0 points)
        assertNull(TRIANGLE.calcIntersections(RAY_CONT_PX_PY), ERR_FIND_INTERSECTIONS);

        // BVA14: Ray on continuation of edge ZERO-PY (0 points)
        assertNull(TRIANGLE.calcIntersections(RAY_CONT_ZERO_PY), ERR_FIND_INTERSECTIONS);

        // BVA15: Ray on continuation of edge ZERO-PX (0 points)
        assertNull(TRIANGLE.calcIntersections(RAY_CONT_ZERO_PX), ERR_FIND_INTERSECTIONS);

        // BVA16: Additional parallel-to-plane miss case (0 points)
        assertNull(TRIANGLE.calcIntersections(RAY_PARALLEL), ERR_FIND_INTERSECTIONS);
    }

    /**
     * Tests the bounding box creation and intersection logic for {@link Triangle}.
     * Verifies the AABB boundaries and tests ray intersections against the AABB
     * (including rays starting outside, inside, on the boundaries, and distance limits).
     */
    @Test
    void testBoundingBox() {
        AABB aabb = TRIANGLE.getBoundingBox();

        // ============ Equivalence Partitions Tests (Box Creation) ==============
        // EP01: Verify the bounding box dimensions (Min and Max points)
        assertEquals(new Point(0, 0, 0), aabb.getMin(),
                "ERROR: Bounding box min point is incorrect");
        assertEquals(new Point(4, 4, 0), aabb.getMax(),
                "ERROR: Bounding box max point is incorrect");

        // -----------------------------------------------------------------------
        // Test Ray-AABB Intersections
        // -----------------------------------------------------------------------
        double maxDistance = Double.POSITIVE_INFINITY;

        // ============ Equivalence Partitions Tests (Intersections) ==============
        // EP11: Ray starts outside and intersects the AABB
        assertTrue(aabb.intersects(new Ray(new Point(2, 2, -10), Vector.AXIS_Z), maxDistance),
                "ERROR: Ray intersecting AABB from outside should return true");

        // EP12: Ray misses the AABB entirely (y is out of range)
        assertFalse(aabb.intersects(new Ray(new Point(2, 10, -10), Vector.AXIS_Z), maxDistance),
                "ERROR: Ray missing the AABB should return false");

        // EP13: Ray intersects the AABB, but intersection is beyond maxDistance
        assertFalse(aabb.intersects(new Ray(new Point(2, 2, -10), Vector.AXIS_Z), 9.0),
                "ERROR: Ray intersecting AABB beyond maxDistance should return false");

        // ============ Boundary Values Tests (Intersections) ==================

        // BVA01: Ray starts strictly INSIDE the AABB
        assertTrue(aabb.intersects(new Ray(new Point(1, 1, 0), Vector.AXIS_Z), maxDistance),
                "ERROR: Ray starting inside the AABB should return true");

        // BVA02: Ray starts ON the AABB boundary (z = 0) pointing OUTWARDS
        // (Since it starts on the boundary, t=0 is a valid intersection)
        assertTrue(aabb.intersects(new Ray(new Point(2, 2, 0), Vector.AXIS_Z), maxDistance),
                "ERROR: Ray starting on the AABB boundary pointing outwards should return true (intersects at t=0)");

        // BVA03: Ray runs parallel to a bounding plane (Z-axis), sliding exactly on the edge (x=4, y=4)
        assertTrue(aabb.intersects(new Ray(new Point(4, 4, -10), Vector.AXIS_Z), maxDistance),
                "ERROR: Ray sliding perfectly along the AABB edge should return true");

        // BVA04: Ray runs parallel to a bounding plane, strictly missing it (just outside x range)
        assertFalse(aabb.intersects(new Ray(new Point(4.1, 4, -10), Vector.AXIS_Z), maxDistance),
                "ERROR: Ray parallel to AABB but slightly outside should return false");

        // BVA05: Ray starts inside, but maxDistance is exactly 0
        assertTrue(aabb.intersects(new Ray(new Point(1, 1, 0), Vector.AXIS_Z), 0.0),
                "ERROR: Ray starting inside with maxDistance 0 should intersect at t=0");
    }

    /**
     * Tests the ray-AABB intersection logic for the triangle's AABB, focusing on maxDistance.
     * Verifies that intersections are correctly identified or rejected based on finite distance limits.
     */
    @Test
    void testAABBDistance() {
        AABB aabb = TRIANGLE.getBoundingBox(); // min=(0,0,0), max=(4,4,0)

        // Ray starts at z = -10, pointing towards +Z.
        // It reaches the box at distance t = 10 (z=0 plane).
        Ray rayOutside = new Ray(new Point(2, 2, -10), Vector.AXIS_Z);

        // ============ Equivalence Partitions Tests ==============

        // EP01: maxDistance is completely before the box (e.g., 9.0 < 10.0)
        assertFalse(aabb.intersects(rayOutside, 9.0),
                "ERROR: Ray should not intersect if maxDistance is shorter than the distance to the box");

        // EP02: maxDistance is exactly at the entry (10.0)
        assertTrue(aabb.intersects(rayOutside, 10.0),
                "ERROR: Ray should intersect if maxDistance reaches the box entry boundary");

        // EP03: maxDistance is beyond the box
        assertTrue(aabb.intersects(rayOutside, 20.0),
                "ERROR: Ray should intersect if maxDistance is beyond the box limits");

        // ============ Boundary Values Tests ==================

        // BVA01: Ray starts strictly inside the box, limited by a very short maxDistance
        Ray rayInside = new Ray(new Point(2, 2, 0), Vector.AXIS_Z);
        assertTrue(aabb.intersects(rayInside, 0.5),
                "ERROR: Ray starting inside should intersect even with a very short maxDistance");

        // BVA02: Ray starts inside/on the boundary, maxDistance is exactly 0.0
        assertTrue(aabb.intersects(rayInside, 0.0),
                "ERROR: Ray starting inside with maxDistance 0.0 should intersect at t=0");

        // BVA03: Ray starts exactly ON the top boundary (z = 0), points outwards, maxDistance is exactly 0.0
        Ray rayOnBoundary = new Ray(new Point(2, 2, 0), Vector.AXIS_Z);
        assertTrue(aabb.intersects(rayOnBoundary, 0.0),
                "ERROR: Ray starting on boundary pointing outwards with maxDistance 0.0 should intersect");
    }

}
