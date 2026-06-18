package renderer;

import lighting.impl.DirectionalLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectionalLightTests {

    private final double DELTA = 1e-10;

    DirectionalLight DL1 = new DirectionalLight(Color.BLACK, Vector.AXIS_Z);

    @Test
    void TestGetL() {

        assertEquals(Vector.AXIS_Z, DL1.getL(Point.ZERO));

    }

    @Test
    void TestGetIntensity() {
        assertEquals(Color.BLACK, DL1.getIntensity(Point.ZERO));

    }
}
