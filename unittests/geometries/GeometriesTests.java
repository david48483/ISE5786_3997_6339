
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

/** * Unit tests for the {@link Geometries} composite geometry class. * * @author David &amp; Yehuda */
public class GeometriesTests {

    /**     * Plane at z=0 with normal in the +Z direction.     */
    private final Plane _plane = new Plane(Point.ZERO, Vector.AXIS_Z);

    /**     * Sphere centered at origin with radius 5.     */
    private final Sphere _sphere = new Sphere(Point.ZERO, 5);

    /**     * Triangle in the z=0 plane with vertices at origin, (4,0,0), (0,4,0).     */
    private final Triangle _triangle = new Triangle(Point.ZERO, new Point(4, 0, 0), new Point(0, 4, 0));

    // ---- Constructor & add ----

    /**     * Test method for {@link Geometries#Geometries(geometries.api.Intersectable...)}.     */
    @Test
    void testConstructor() {
        assertDoesNotThrow(
                () -> new Geometries(_plane, _sphere, _triangle),
                "ERROR: Geometries constructor threw unexpectedly");
    }

    /**     * Test method for {@link Geometries#add(geometries.api.Intersectable...)}.     */
    @Test
    void testAdd() {
        assertDoesNotThrow(() -> {
            Geometries g = new Geometries(_plane);
            g.add(_sphere, _triangle);
        }, "ERROR: add() threw unexpectedly");
    }

    // ---- findIntersections ----

    /**     * Test method for {@link Geometries#findIntersections(Ray)}.     */
    @Test
    void testFindIntersections() {
        Geometries geometries = new Geometries(_plane, _sphere, _triangle);

        // ============ Equivalence Partitions Tests ============

        // EP01: Some (but not all) geometries are intersected.
        // Ray from (3,0,-10) going +Z:
        //   Sphere  d²=9 < 25 → 2 intersection points
        //   Plane   z=0, t=10 > 0 → 1 intersection point at (3,0,0)
        //   Triangle: point (3,0,0) lies on edge p1-p2 (boundary) → no intersection (returns null)
        // Total expected: 3
        assertEquals(3,
                geometries.findIntersections(new Ray(new Point(3, 0, -10), Vector.AXIS_Z)).size(),
                "EP01: wrong number of intersections when some geometries are hit");

        // ============ Boundary Values Tests ============

        // BVA01: No geometry is intersected → must return null.
        // Ray from (10,0,6) going +X passes entirely outside and above the sphere.
        assertNull(
                geometries.findIntersections(new Ray(new Point(10, 0, 1), Vector.AXIS_X)),
                "BVA01: expected null when no geometry is intersected");

        // BVA02: Exactly one geometry is intersected.
        // Ray from (0,0,-10) going +Z through sphere only (d²=0 < 25 → 2 pts),
        // but plane at (0,0,0) and triangle vertex at (0,0,0) are edge cases.
        // Use a Geometries with only the sphere to isolate the single-geometry BVA:
        assertEquals(2,
                geometries.findIntersections(new Ray(new Point(-6, 2, 2), Vector.AXIS_X)).size(),
                "BVA02: wrong count when exactly one geometry is intersected");

        // BVA03: All geometries are intersected.
        // Ray from (1,1,-10) going +Z:
        //   Sphere  d²=2 < 25 → 2 intersection points
        //   Plane   z=0, t=10 > 0 → 1 intersection point at (1,1,0)
        //   Triangle: point (1,1,0) is inside triangle (1+1=2 < 4) → 1 intersection point
        // Total expected: 4
        assertEquals(4,
                geometries.findIntersections(new Ray(new Point(1, 1, -10), Vector.AXIS_Z)).size(),
                "BVA03: wrong number of intersections when all geometries are hit");
    }
}