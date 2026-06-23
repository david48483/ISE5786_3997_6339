package renderer;

import lighting.impl.DirectionalLight;
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
     * Tolerance constant reserved for floating-point comparisons in this suite.
     */
    private final double DELTA = 1e-10;

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

        assertEquals(Vector.AXIS_Z, DL1.getL(Point.ZERO));

    }

    /**
     * Verifies that directional light intensity is distance-independent.
     */
    @Test
    void TestGetIntensity() {
        assertEquals(Color.BLACK, DL1.getIntensity(Point.ZERO));

    }
}
