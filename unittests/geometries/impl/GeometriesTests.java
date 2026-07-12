package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Geometries} composite geometry class.
 * Covers the constructor, {@link Geometries#add} and
 * {@link Geometries#findIntersections(Ray)}.
 * Tests follow the Equivalence Partitions (EP) and Boundary Values Analysis (BVA) methodology.
 *
 * @author David &amp; Yehuda
 */
public class GeometriesTests {

    /**
     * Default constructor for GeometriesTests.
     */
    public GeometriesTests() {
    }

    // ---- Error messages ----

    /**
     * Error message for {@link Geometries#Geometries(geometries.api.Intersectable...)} tests.
     */
    private static final String ERR_CONSTRUCTOR =
            "ERROR: Geometries constructor threw unexpectedly";

    /**
     * Error message for {@link Geometries#add(geometries.api.Intersectable...)} tests.
     */
    private static final String ERR_ADD =
            "ERROR: add() threw unexpectedly";

    /**
     * Error message when the wrong number of intersections is returned.
     */
    private static final String ERR_INTERSECTIONS =
            "ERROR: Geometries findIntersections() returned wrong number of intersections";

    // ---- Shared geometries ----

    /**
     * Plane at z=0 with normal in the +Z direction.
     */
    private final Plane _plane = new Plane(Point.ZERO, Vector.AXIS_Z);

    /**
     * Sphere centered at the origin with radius 5.
     */
    private final Sphere _sphere = new Sphere(Point.ZERO, 5);

    /**
     * Triangle in the z=0 plane with vertices at origin, (4,0,0) and (0,4,0).
     */
    private final Triangle _triangle = new Triangle(Point.ZERO, new Point(4, 0, 0), new Point(0, 4, 0));

    // ---- Shared rays ----

    /**
     * EP01 — from (3,0,−10) going +Z: hits sphere (2 pts) and plane (1 pt); misses triangle.
     * Total expected intersections: 3.
     */
    private final Ray _raySome = new Ray(new Point(3, 0, -10), Vector.AXIS_Z);

    /**
     * BVA01 — from (10,0,6) going +X: passes entirely above and outside the sphere.
     * Expected result: null.
     */
    private final Ray _rayNone = new Ray(new Point(10, 0, 6), Vector.AXIS_X);

    /**
     * BVA02 — from (0,0,3) going +X: starts inside the sphere and exits once (1 pt);
     * travels at z=3 so it never reaches the z=0 plane or triangle.
     * Total expected intersections: 1.
     */
    private final Ray _rayOne = new Ray(new Point(0, 0, 3), Vector.AXIS_X);

    /**
     * BVA03 — from (1,1,−10) going +Z: hits sphere (2 pts), plane (1 pt) and triangle (1 pt).
     * Total expected intersections: 4.
     */
    private final Ray _rayAll = new Ray(new Point(1, 1, -10), Vector.AXIS_Z);

    // ---- Tests ----

    /**
     * Test method for {@link Geometries#Geometries(geometries.api.Intersectable...)}.
     * Verifies that constructing a composite from valid geometries does not throw.
     */
    @Test
    void testConstructor() {
        assertDoesNotThrow(
                () -> new Geometries(_plane, _sphere, _triangle),
                ERR_CONSTRUCTOR);
    }

    /**
     * Test method for {@link Geometries#add(geometries.api.Intersectable...)}.
     * Verifies that adding valid geometries to an existing composite does not throw.
     */
    @Test
    void testAdd() {
        assertDoesNotThrow(() -> {
            Geometries g = new Geometries(_plane);
            g.add(_sphere, _triangle);
        }, ERR_ADD);
    }

    /**
     * Test method for {@link Geometries#findIntersections(Ray)}.
     * Covers the cases where some geometries are hit (EP), no geometry is hit,
     * exactly one geometry is hit, and all geometries are hit (BVA).
     */
    @Test
    void testFindIntersections() {
        Geometries geometries = new Geometries(_plane, _sphere, _triangle);

        // ============ Equivalence Partitions Tests ============

        // EP01: Some (but not all) geometries are intersected — sphere + plane, not triangle
        assertEquals(3, geometries.findIntersections(_raySome).size(), ERR_INTERSECTIONS);

        // ============ Boundary Values Tests ============

        // BVA01: No geometry is intersected → must return null
        assertNull(geometries.findIntersections(_rayNone), ERR_INTERSECTIONS);

        // BVA02: Exactly one geometry is intersected — sphere only
        assertEquals(1, geometries.findIntersections(_rayOne).size(), ERR_INTERSECTIONS);

        // BVA03: All geometries are intersected — sphere (2) + plane (1) + triangle (1)
        assertEquals(4, geometries.findIntersections(_rayAll).size(), ERR_INTERSECTIONS);
    }

    /**
     * Error message when the wrong number of intersections is returned by calcIntersections.
     */
    private static final String ERR_CALC_INTERSECTIONS =
            "ERROR: Geometries calcIntersections() returned wrong number of intersections";

    /**
     * Test method for {@link Geometries#calcIntersections(Ray)}.
     * Covers the cases where some geometries are hit (EP), no geometry is hit,
     * exactly one geometry is hit, and all geometries are hit (BVA).
     */
    @Test
    void testCalcIntersections() {
        Geometries geometries = new Geometries(_plane, _sphere, _triangle);

        // ============ Equivalence Partitions Tests ============

        // EP01: Some (but not all) geometries are intersected — sphere + plane, not triangle
        var resultEP01 = geometries.calcIntersections(_raySome);
        assertNotNull(resultEP01, ERR_CALC_INTERSECTIONS);
        assertEquals(3, resultEP01.size(), ERR_CALC_INTERSECTIONS);

        // ============ Boundary Values Tests ============

        // BVA01: No geometry is intersected → must return null
        assertNull(geometries.calcIntersections(_rayNone), ERR_CALC_INTERSECTIONS);

        // BVA02: Exactly one geometry is intersected — sphere only
        var resultBVA02 = geometries.calcIntersections(_rayOne);
        assertNotNull(resultBVA02, ERR_CALC_INTERSECTIONS);
        assertEquals(1, resultBVA02.size(), ERR_CALC_INTERSECTIONS);

        // BVA03: All geometries are intersected — sphere (2) + plane (1) + triangle (1)
        var resultBVA03 = geometries.calcIntersections(_rayAll);
        assertNotNull(resultBVA03, ERR_CALC_INTERSECTIONS);
        assertEquals(4, resultBVA03.size(), ERR_CALC_INTERSECTIONS);
    }
}