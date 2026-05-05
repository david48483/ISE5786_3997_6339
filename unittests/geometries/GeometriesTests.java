package geometries;

import geometries.impl.Geometries;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
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

    // ---- Constructor & add ----

    /**
     * Test method for {@link Geometries#Geometries(geometries.api.Intersectable...)}.
     * Verifies that constructing a composite from valid geometries does not throw.
     */
    @Test
    void testConstructor() {
        assertDoesNotThrow(
                () -> new Geometries(_plane, _sphere, _triangle),
                "ERROR: Geometries constructor threw unexpectedly");
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
        }, "ERROR: add() threw unexpectedly");
    }

    // ---- findIntersections ----

    /**
     * Test method for {@link Geometries#findIntersections(Ray)}.
     * Covers the cases where some geometries are hit (EP), no geometry is hit,
     * exactly one geometry is hit, and all geometries are hit (BVA).
     */
    @Test
    void testFindIntersections() {
        Geometries geometries = new Geometries(_plane, _sphere, _triangle);

        // ============ Equivalence Partitions Tests ============

        // EP01: Some (but not all) geometries are intersected.
        // Ray from (3,0,-10) going +Z:
        //   Sphere  d²=9 < 25 → 2 intersection points
        //   Plane   z=0 → 1 intersection point at (3,0,0)
        //   Triangle: (3,0,0) lies on edge origin-PX (boundary) → not intersected
        // Total expected: 3
        assertEquals(3,
                geometries.findIntersections(new Ray(new Point(3, 0, -10), Vector.AXIS_Z)).size(),
                "EP01: wrong number of intersections when some geometries are hit");

        // ============ Boundary Values Tests ============

        // BVA01: No geometry is intersected → must return null.
        // Ray from (10,0,6) going +X passes entirely above and outside the sphere.
        assertNull(
                geometries.findIntersections(new Ray(new Point(10, 0, 6), Vector.AXIS_X)),
                "BVA01: expected null when no geometry is intersected");

        // BVA02: Exactly one geometry is intersected.
        // Ray from (0,0,3) going +X: starts inside the sphere, exits once (1 point);
        // travels at z=3 in the X direction → never reaches the z=0 plane or triangle.
        assertEquals(1,
                geometries.findIntersections(new Ray(new Point(0, 0, 3), Vector.AXIS_X)).size(),
                "BVA02: wrong count when exactly one geometry is intersected");

        // BVA03: All geometries are intersected.
        // Ray from (1,1,-10) going +Z:
        //   Sphere  d²=2 < 25 → 2 intersection points
        //   Plane   z=0 → 1 intersection point at (1,1,0)
        //   Triangle: (1,1,0) satisfies 1+1=2 < 4, strictly inside → 1 intersection point
        // Total expected: 4
        assertEquals(4,
                geometries.findIntersections(new Ray(new Point(1, 1, -10), Vector.AXIS_Z)).size(),
                "BVA03: wrong number of intersections when all geometries are hit");
    }
}