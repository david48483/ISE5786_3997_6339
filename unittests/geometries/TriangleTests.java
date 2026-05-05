package geometries;

import geometries.impl.Triangle;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    // ---- Error messages ----

    /** Error message for {@link Triangle#getNormal(Point)} tests. */
    private static final String ERR_GET_NORMAL =
            "ERROR: Triangle getNormal() returned wrong normal";

    /** Error message for {@link Triangle#findIntersections(Ray)} tests. */
    private static final String ERR_FIND_INTERSECTIONS =
            "ERROR: Triangle findIntersections() returned wrong result";

    // ---- Shared geometry ----

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

    // ---- Shared points ----

    /**
     * A point strictly inside the triangle, used for getNormal.
     */
    private static final Point P_INSIDE = new Point(1, 1, 0);

    /**
     * Expected intersection point for EP01: (2,1,0).
     */
    private static final Point P_HIT = new Point(2, 1, 0);

    // ---- EP rays — miss / hit ----

    /** EP01: Ray from (2,1,−1) +Z — hits strictly inside the triangle. */
    private static final Ray RAY_HIT              = new Ray(new Point(2,  1, -1), Vector.AXIS_Z);

    /** EP02: Ray from (5,5,−1) +Z — misses; opposite edge PY–PX. */
    private static final Ray RAY_OPP_EDGE_PY_PX   = new Ray(new Point(5,  5, -1), Vector.AXIS_Z);

    /** EP03: Ray from (5,−2,−1) +Z — misses; opposite edge ZERO–PX. */
    private static final Ray RAY_OPP_EDGE_ZERO_PX = new Ray(new Point(5, -2, -1), Vector.AXIS_Z);

    /** EP04: Ray from (−2,5,−1) +Z — misses; opposite edge ZERO–PY. */
    private static final Ray RAY_OPP_EDGE_ZERO_PY = new Ray(new Point(-2, 5, -1), Vector.AXIS_Z);

    /** EP05: Ray from (6,−1,−1) +Z — misses; opposite vertex PX. */
    private static final Ray RAY_OPP_VERTEX_PX    = new Ray(new Point(6, -1, -1), Vector.AXIS_Z);

    /** EP06: Ray from (−1,6,−1) +Z — misses; opposite vertex PY. */
    private static final Ray RAY_OPP_VERTEX_PY    = new Ray(new Point(-1, 6, -1), Vector.AXIS_Z);

    /** EP07: Ray from (−1,−1,−1) +Z — misses; opposite vertex ZERO. */
    private static final Ray RAY_OPP_VERTEX_ZERO  = new Ray(new Point(-1, -1, -1), Vector.AXIS_Z);

    /** EP08: Ray from (1,1,−1) +X — parallel to the triangle plane. */
    private static final Ray RAY_PARALLEL         = new Ray(new Point(1,  1, -1), Vector.AXIS_X);

    // ---- BVA rays — edges ----

    /** BVA11: Ray from (2,2,−1) +Z — hits edge PX–PY. */
    private static final Ray RAY_ON_EDGE_PX_PY    = new Ray(new Point(2, 2, -1), Vector.AXIS_Z);

    /** BVA12: Ray from (0,2,−1) +Z — hits edge ZERO–PY. */
    private static final Ray RAY_ON_EDGE_ZERO_PY  = new Ray(new Point(0, 2, -1), Vector.AXIS_Z);

    /** BVA13: Ray from (2,0,−1) +Z — hits edge ZERO–PX. */
    private static final Ray RAY_ON_EDGE_ZERO_PX  = new Ray(new Point(2, 0, -1), Vector.AXIS_Z);

    // ---- BVA rays — vertices ----

    /** BVA21: Ray from (4,0,−1) +Z — hits vertex PX. */
    private static final Ray RAY_ON_VERTEX_PX     = new Ray(new Point(4, 0, -1), Vector.AXIS_Z);

    /** BVA22: Ray from (0,4,−1) +Z — hits vertex PY. */
    private static final Ray RAY_ON_VERTEX_PY     = new Ray(new Point(0, 4, -1), Vector.AXIS_Z);

    /** BVA23: Ray from (0,0,−1) +Z — hits vertex ZERO. */
    private static final Ray RAY_ON_VERTEX_ZERO   = new Ray(new Point(0, 0, -1), Vector.AXIS_Z);

    // ---- BVA rays — edge continuations ----

    /** BVA31: Ray from (5,−1,−1) +Z — on continuation of edge PX–PY. */
    private static final Ray RAY_CONT_PX_PY       = new Ray(new Point(5, -1, -1), Vector.AXIS_Z);

    /** BVA32: Ray from (0,5,−1) +Z — on continuation of edge ZERO–PY. */
    private static final Ray RAY_CONT_ZERO_PY     = new Ray(new Point(0,  5, -1), Vector.AXIS_Z);

    /** BVA33: Ray from (5,0,−1) +Z — on continuation of edge ZERO–PX. */
    private static final Ray RAY_CONT_ZERO_PX     = new Ray(new Point(5,  0, -1), Vector.AXIS_Z);

    // ---- Tests ----

    /**
     * Test method for {@link Triangle#getNormal(Point)}.
     * Verifies that the normal vector is correct for a point on the triangle.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Point strictly inside the triangle — normal must equal +Z
        assertEquals(Vector.AXIS_Z, TRIANGLE.getNormal(P_INSIDE), ERR_GET_NORMAL);
    }

    /**
     * Test method for {@link Triangle#findIntersections(Ray)}.
     * Verifies intersection detection for rays that hit inside the triangle,
     * miss it (opposite edge or vertex), lie on an edge or vertex,
     * and hit the edge continuation.
     */
    @Test
    void findIntersections() {
        // ============ Equivalence Partitions Tests ==============

        // **** Group 1: ray intersects inside the triangle
        // EP01: ray hits strictly inside — 1 point
        assertEquals(List.of(P_HIT), TRIANGLE.findIntersections(RAY_HIT), ERR_FIND_INTERSECTIONS);

        // **** Group 2: ray hits the triangle plane but misses — opposite edges
        // EP02: opposite edge PY–PX
        assertNull(TRIANGLE.findIntersections(RAY_OPP_EDGE_PY_PX),   ERR_FIND_INTERSECTIONS);
        // EP03: opposite edge ZERO–PX
        assertNull(TRIANGLE.findIntersections(RAY_OPP_EDGE_ZERO_PX), ERR_FIND_INTERSECTIONS);
        // EP04: opposite edge ZERO–PY
        assertNull(TRIANGLE.findIntersections(RAY_OPP_EDGE_ZERO_PY), ERR_FIND_INTERSECTIONS);

        // **** Group 3: ray hits the triangle plane but misses — opposite vertices
        // EP05: opposite vertex PX
        assertNull(TRIANGLE.findIntersections(RAY_OPP_VERTEX_PX),   ERR_FIND_INTERSECTIONS);
        // EP06: opposite vertex PY
        assertNull(TRIANGLE.findIntersections(RAY_OPP_VERTEX_PY),   ERR_FIND_INTERSECTIONS);
        // EP07: opposite vertex ZERO
        assertNull(TRIANGLE.findIntersections(RAY_OPP_VERTEX_ZERO), ERR_FIND_INTERSECTIONS);
        // EP08: ray parallel to the triangle plane
        assertNull(TRIANGLE.findIntersections(RAY_PARALLEL),        ERR_FIND_INTERSECTIONS);

        // ============ Boundary Values Tests ==============

        // **** Group 1: ray hits an edge — 0 points
        // BVA11: edge PX–PY
        assertNull(TRIANGLE.findIntersections(RAY_ON_EDGE_PX_PY),   ERR_FIND_INTERSECTIONS);
        // BVA12: edge ZERO–PY
        assertNull(TRIANGLE.findIntersections(RAY_ON_EDGE_ZERO_PY), ERR_FIND_INTERSECTIONS);
        // BVA13: edge ZERO–PX
        assertNull(TRIANGLE.findIntersections(RAY_ON_EDGE_ZERO_PX), ERR_FIND_INTERSECTIONS);

        // **** Group 2: ray hits a vertex — 0 points
        // BVA21: vertex PX
        assertNull(TRIANGLE.findIntersections(RAY_ON_VERTEX_PX),   ERR_FIND_INTERSECTIONS);
        // BVA22: vertex PY
        assertNull(TRIANGLE.findIntersections(RAY_ON_VERTEX_PY),   ERR_FIND_INTERSECTIONS);
        // BVA23: vertex ZERO
        assertNull(TRIANGLE.findIntersections(RAY_ON_VERTEX_ZERO), ERR_FIND_INTERSECTIONS);

        // **** Group 3: ray hits the continuation of an edge — 0 points
        // BVA31: continuation of edge PX–PY
        assertNull(TRIANGLE.findIntersections(RAY_CONT_PX_PY),    ERR_FIND_INTERSECTIONS);
        // BVA32: continuation of edge ZERO–PY
        assertNull(TRIANGLE.findIntersections(RAY_CONT_ZERO_PY),  ERR_FIND_INTERSECTIONS);
        // BVA33: continuation of edge ZERO–PX
        assertNull(TRIANGLE.findIntersections(RAY_CONT_ZERO_PX),  ERR_FIND_INTERSECTIONS);
    }
}

