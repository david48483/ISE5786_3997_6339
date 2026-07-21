package geometries.impl;

import org.junit.jupiter.api.Test;
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
}