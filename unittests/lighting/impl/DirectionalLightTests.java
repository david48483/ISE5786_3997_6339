package lighting.impl;

import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link lighting.impl.DirectionalLight}.
 *
 * @author David &amp; Yehuda
 */
public class DirectionalLightTests {

    /**
     * Directional light fixture used by the tests.
     */
    DirectionalLight DL1 = new DirectionalLight(Color.BLACK, Vector.AXIS_Z);

    /**
     * Default constructor for the test suite.
     */
    public DirectionalLightTests() {
    }

    /**
     * Verifies that {@code getL} returns the stored light direction.
     */
    @Test
    void TestGetL() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Regular point in space.

        assertEquals(Vector.AXIS_Z, DL1.getL(Point.ZERO));

    }

    /**
     * Verifies that directional light intensity is distance-independent.
     */
    @Test
    void TestGetIntensity() {
        // ============ Equivalence Partitions Tests ==============
        //  TC01: Regular point in space.
        assertEquals(Color.BLACK, DL1.getIntensity(Point.ZERO));

    }
}
