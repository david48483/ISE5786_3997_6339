package renderer;

import lighting.impl.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link SpotLight}.
 *
 * @author David &amp; Yehuda
 */
public class SpotLightTests {

    /**
     * Base light color used across spotlight tests.
     */
    Color base = new Color(100, 100, 100);

    /**
     * Spotlight fixture used by the tests.
     */
    SpotLight light = new SpotLight(base, Point.ZERO, Vector.AXIS_Z);

    /**
     * Creates a spotlight test suite instance.
     */
    public SpotLightTests() {
    }

    /**
     * Verifies direction-vector behavior returned by {@link SpotLight#getL(Point)}.
     */
    @Test
    void TestGetL() {

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
        double sqrt2_div2 = Math.sqrt(2) / 2;
        assertEquals(new Vector(sqrt2_div2, 0, sqrt2_div2), light.getL(new Point(3, 0, 3)),
                "getL should return normalized vector for a point on the 90-degree boundary");

        //BV02 The object is at 90 degrees to the direction of the light.
        assertEquals(Vector.AXIS_Y, light.getL(new Point(0, 1, 0)),
                "getL should return normalized vector from light to point");

    }

    /**
     * Verifies spotlight-intensity behavior returned by {@link SpotLight#getIntensity(Point)}.
     */
    @Test
    void TestGetIntensity() {
        //============ Equivalence Partitions Tests ==============
        //EP01
        light.setKc(1.0).setKl(0.5).setKq(0.0);

        assertEquals(base.scale(0.5), light.getIntensity(new Point(0, 0, 2)),
                "Intensity should be base color when point is in the direction of the light");
        //EP02
        assertEquals(Color.BLACK, light.getIntensity(new Point(0, 0, -2)),
                "Intensity should be black when point is opposite to the direction of the light");

        //============ Boundary Values Tests ==============
        //BV01: The object is at 90 degrees to the direction of the light.
        assertEquals(Color.BLACK, light.getIntensity(new Point(0, 1, 0)),
                "Intensity should be black when point is at 90 degrees to the direction of the light");

        //  BV02:   The object is at the position of the light.
        assertEquals(base, light.getIntensity(Point.ZERO),
                "Intensity should be base color when point is at the position of the light");

    }
}
