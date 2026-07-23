package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.AABB;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link Polygon}.
 * The tests verify:
 * <ul>
 * <li>Polygon constructor validity</li>
 * <li>{@link Polygon#getNormal(Point)}</li>
 * <li>{@link Polygon#findIntersections(Ray)}</li>
 * <li>{@link Polygon#calcIntersections(Ray)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 *
 * @author David &amp; Yehuda
 */
class PolygonTests {

    /**
     * Default constructor to satisfy JavaDoc generator
     */
    PolygonTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Vertex (1,0,0) used in polygon tests
     */
    private static final Point POINT_X = new Point(1, 0, 0);
    /**
     * Vertex (0,1,0) used in polygon tests
     */
    private static final Point POINT_Y = new Point(0, 1, 0);
    /**
     * Vertex (0,0,1) used in polygon tests
     */
    private static final Point POINT_Z = new Point(0, 0, 1);

    /**
     * Additional vertex used for valid polygon construction
     */
    private static final Point POINT1 = new Point(-1, 1, 1);
    /**
     * Point not in the polygon plane
     */
    private static final Point POINT2 = new Point(0, 2, 2);
    /**
     * Point that creates a concave polygon
     */
    private static final Point POINT3 = new Point(0.5, 0.25, 0.5);
    /**
     * Point located on one of the polygon edges
     */
    private static final Point POINT4 = new Point(0, 0.5, 0.5);

    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Polygon in the XY plane used for intersection tests
     */
    private static final Polygon POLYGON_XY = new Polygon(
            new Point(0, 0, 0),
            new Point(4, 0, 0),
            new Point(4, 4, 0),
            new Point(0, 4, 0)
    );

    /**
     * Point inside the POLYGON_XY
     */
    private static final Point P_INSIDE = new Point(2, 2, 0);

    /**
     * Ray hitting strictly inside the POLYGON_XY
     */
    private static final Ray RAY_HIT = new Ray(new Point(2, 2, -2), Vector.AXIS_Z);

    /**
     * Error message for wrong polygon intersection
     */
    private static final String ERROR_POLYGON = "ERROR: wrong polygon intersection";

    /**
     * Test method for {@link Polygon#Polygon(Point...)}.
     * Verifies correct and incorrect polygon constructions.
     */
    @Test
    void testConstructor() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: Correct convex quadrilateral with vertices in correct order
        assertDoesNotThrow(() -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT1),
                "Failed constructing a correct polygon");

        // TC02: Wrong vertices order
        assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_Y, POINT_X, POINT1),
                "Constructed a polygon with wrong order of vertices");

        // TC03: Vertices not in the same plane
        assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT2),
                "Constructed a polygon with vertices that are not in the same plane");

        // TC04: Concave quadrilateral
        assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT3),
                "Constructed a concave polygon");

        // =============== Boundary Values Tests ==================

        // TC11: Vertex on a side
        assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT4),
                "Constructed a polygon with a vertex on a side");

        // TC12: Last point equals first point
        assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT_Z),
                "Constructed a polygon with duplicate first/last vertex");

        // TC13: Co-located points
        assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT_Y),
                "Constructed a polygon with co-located vertices");
    }

    /**
     * Test method for {@link Polygon#getNormal(Point)}.
     * Verifies that the returned normal vector is unit length and orthogonal
     * to all polygon edges.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        Point[] pts = {POINT_Z, POINT_X, POINT_Y, POINT1};
        Polygon polygon = new Polygon(pts);
        // Ensure method does not throw exception
        assertDoesNotThrow(() -> polygon.getNormal(POINT_Z), "getNormal() threw unexpected exception");
        Vector result = polygon.getNormal(POINT_Z);
        // Ensure |n| = 1
        assertEquals(1, result.length(), DELTA, "Polygon normal is not a unit vector");
        // Ensure normal is orthogonal to all edges
        for (int i = 0; i < pts.length; ++i) {
            Vector edge = pts[i].subtract(pts[i == 0 ? pts.length - 1 : i - 1]);
            assertEquals(0d, result.dotProduct(edge), DELTA, "Polygon normal is not orthogonal to an edge");
        }
    }

    /**
     * Test method for {@link Polygon#findIntersections(Ray)}.
     * Verifies that the method correctly calculates intersection points for a polygon.
     */
    @Test
    void testFindIntersections() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray intersects strictly inside the polygon (1 point)
        assertEquals(List.of(P_INSIDE), POLYGON_XY.findIntersections(RAY_HIT), ERROR_POLYGON);

        // EP02: Ray misses the polygon outside an edge
        assertNull(POLYGON_XY.findIntersections(new Ray(new Point(5, 2, -2), Vector.AXIS_Z)), ERROR_POLYGON);

        // EP03: Ray misses the polygon outside a vertex
        assertNull(POLYGON_XY.findIntersections(new Ray(new Point(5, 5, -2), Vector.AXIS_Z)), ERROR_POLYGON);

        // EP04: Ray points away from the polygon plane
        assertNull(POLYGON_XY.findIntersections(new Ray(new Point(2, 2, 2), Vector.AXIS_Z)), ERROR_POLYGON);

        // ============ Boundary Values Tests ==============

        // BVA01: Ray hits an edge of the polygon (0 points)
        assertNull(POLYGON_XY.findIntersections(new Ray(new Point(4, 2, -2), Vector.AXIS_Z)), ERROR_POLYGON);

        // BVA02: Ray hits a vertex of the polygon (0 points)
        assertNull(POLYGON_XY.findIntersections(new Ray(new Point(4, 4, -2), Vector.AXIS_Z)), ERROR_POLYGON);

        // BVA03: Ray hits the continuation of an edge (0 points)
        assertNull(POLYGON_XY.findIntersections(new Ray(new Point(6, 0, -2), Vector.AXIS_Z)), ERROR_POLYGON);
    }

    /**
     * Test method for {@link Polygon#calcIntersections(Ray, double)}.
     * Verifies GeoPoint structure and maxDistance boundaries.
     */
    @Test
    void testCalcIntersections() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray intersects inside the polygon
        var resultEP01 = POLYGON_XY.calcIntersections(RAY_HIT);
        assertNotNull(resultEP01, ERROR_POLYGON);
        assertEquals(1, resultEP01.size(), ERROR_POLYGON);
        assertSame(POLYGON_XY, resultEP01.getFirst().geometry, ERROR_POLYGON);
        assertEquals(P_INSIDE, resultEP01.getFirst().point, ERROR_POLYGON);

        // EP02: Ray intersection distance is longer than maxDistance
        assertNull(POLYGON_XY.calcIntersections(RAY_HIT, 1.0), ERROR_POLYGON);

        // EP03: Ray intersection distance is within maxDistance
        var resultEP03 = POLYGON_XY.calcIntersections(RAY_HIT, 3.0);
        assertNotNull(resultEP03, ERROR_POLYGON);
        assertEquals(1, resultEP03.size(), ERROR_POLYGON);

        // ============ Boundary Values Tests ==============

        // BVA01: Ray starts on the polygon plane (0 points)
        assertNull(POLYGON_XY.calcIntersections(new Ray(P_INSIDE, Vector.AXIS_Z)), ERROR_POLYGON);
    }

    /**
     * Tests the bounding box creation and intersection logic for {@link Polygon}.
     * Verifies the AABB boundaries and tests ray intersections against the AABB
     * (including rays starting outside, inside, on the boundaries, and distance limits).
     */
    @Test
    void testBoundingBox() {
        AABB aabb = POLYGON_XY.getBoundingBox();

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
     * Tests the ray-AABB intersection logic for the polygon's AABB, focusing on maxDistance.
     * Verifies that intersections are correctly identified or rejected based on finite distance limits.
     */
    @Test
    void testAABBDistance() {
        AABB aabb = POLYGON_XY.getBoundingBox(); // min=(0,0,0), max=(4,4,0)

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