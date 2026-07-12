package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

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

        // ============ Boundary Values Tests ==============

        // BVA01: Ray is parallel and included in the plane (0 points)
        assertNull(TRIANGLE.calcIntersections(new Ray(P_IN_PLANE_INSIDE, Vector.AXIS_X)), ERR_FIND_INTERSECTIONS);

        // BVA02: Ray is parallel and not included in the plane (0 points)
        assertNull(TRIANGLE.calcIntersections(new Ray(new Point(2, 1, 1), Vector.AXIS_X)), ERR_FIND_INTERSECTIONS);

        // BVA03: Ray is orthogonal to the plane and starts before the plane (1 point)
        var resultBVA03 = TRIANGLE.calcIntersections(RAY_HIT);
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

}
