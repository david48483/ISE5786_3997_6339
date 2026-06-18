package renderer;

import lighting.impl.PointLight;
import lighting.impl.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpotLightTests {

    @Test
    void TestGetL() {

        PointLight light = new SpotLight(new Color(100, 100, 100), Point.ZERO, Vector.AXIS_Z);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Regular point in space.
        // assertEquals(Vector.AXIS_X, light.getL(new Point(2, 0, 0)),
        //      "getL should return normalized vector from light to point");

        //EP01:
        assertEquals(Vector.AXIS_Z, light.getL(new Point(0, 0, 2)),
                "getL should return normalized vector from light to point");

        //EP02
        assertEquals(new Vector(0, 0, -1), light.getL(new Point(0, 0, -2)),
                "getL should return normalized vector from light to point");

        // =============== Boundary Values Tests ==================
        // VB01: Very close point (non-zero distance).
        assertThrows(IllegalArgumentException.class, () -> light.getL(Point.ZERO),
                "getL should throw exception for point at light position");

        //BV02 The object is at 90 degrees to the direction of the light.
        assertEquals(new Vector(0, 1, 0), light.getL(new Point(0, 1, 0)),
                "getL should return normalized vector from light to point");

    }

    @Test
    void TestGetIntensity() {

    }
}
