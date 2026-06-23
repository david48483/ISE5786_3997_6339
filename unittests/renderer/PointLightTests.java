package renderer;

import lighting.impl.PointLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link lighting.impl.PointLight}.
 *
 * @author David &amp; Yehuda
 */
public class PointLightTests {

    /**
     * Default constructor for the test suite.
     */
    public PointLightTests() {
    }

    /**
     * Verifies direction vector behavior returned by {@code getL}.
     */
    @Test
    void TestGetL() {

        PointLight light = new PointLight(new Color(100, 100, 100), Point.ZERO);

        // ============ Equivalence Partitions Tests ==============
        // TC01: Regular point in space.
        assertEquals(Vector.AXIS_X, light.getL(new Point(2, 0, 0)),
                "getL should return normalized vector from light to point");

        // =============== Boundary Values Tests ==================
        // TC02: Very close point (non-zero distance).
        assertThrows(IllegalArgumentException.class, () -> light.getL(Point.ZERO),
                "getL should throw exception for point at light position");

    }

    /**
     * Verifies point-light attenuation behavior in {@code getIntensity}.
     */
    @Test
    void TestGetIntensity() {
        Color base = new Color(100, 100, 100);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Linear attenuation.
        PointLight linear = new PointLight(base, Point.ZERO).setKc(1.0).setKl(0.5).setKq(0.0);
        // d=2 => attenuation = 1 + 0.5*2 = 2 -> scale 0.5
        assertEquals(base.scale(0.5), linear.getIntensity(new Point(2, 0, 0)),
                "Linear attenuation should reduce intensity by expected factor");

        // =============== Boundary Values Tests ==================
        // VB01: Quadratic attenuation with very close point.
        assertEquals(base, linear.getIntensity(Point.ZERO),
                "With only kC=1, intensity should remain unchanged");

    }
}
