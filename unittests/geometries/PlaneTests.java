package geometries;

import geometries.impl.Plane;
import org.junit.jupiter.api.Test;
import primitives.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlaneTests {

    @Test
    void testConstructor() {
        assertEquals(12, new Plane(new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0, 0)),
                "ERROR: Plane constructor from three points did not create the expected plane");
    }
}
