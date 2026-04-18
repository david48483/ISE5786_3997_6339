package geometries;

import geometries.impl.Sphere;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for class {@link Sphere}.
 * The tests verify:
 * <ul>
 * <li>{@link Sphere#getNormal(Point)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
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
     * Test method for {@link Sphere#getNormal(Point)}.
     * check that the normal vector is correct for points on the sphere.
     */
    @Test
    void testGetNormal() {

        // ============ Equivalence Partitions Tests ==============

        // create a simple sphere
        Sphere sphere = new Sphere(Point.ZERO, 7);

        //TC01  a regular check
        assertEquals(Vector.AXIS_Z, sphere.getNormal(new Point(0, 0, 7)),
                "ERROR: Sphere getNormal() returned wrong normal for a point on the sphere");

    }
}
