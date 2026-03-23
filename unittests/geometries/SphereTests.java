package geometries;

import geometries.impl.Sphere;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SphereTests {

    @Test
    void testGetNormal() {
        Sphere sphere = new Sphere(Point.ZERO, 7);

        Vector expectedNormal = new Vector(0, 0, 1);

        assertEquals(expectedNormal, sphere.getNormal(new Point(0, 0, 7)),
                "ERROR: Sphere getNormal() returned wrong normal for a point on the sphere");
    }
}
