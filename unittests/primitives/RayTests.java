package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RayTests {

    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============
        assertDoesNotThrow(() -> new Ray(new Point(1, 2, 3), new Vector(0, 0, 1)),
                "ERROR: Ray constructor failed to create the expected ray");

        Ray ray = new Ray(new Point(1, 2, 3), new Vector(4, 0, 0));

        assertEquals(new Vector(1, 0, 0), ray.direction(),
                "ERROR: Ray constructor failed to normalize the direction vector");

        assertEquals(new Point(1, 2, 3), ray.origin(),
                "ERROR: Ray constructor failed to set the origin point correctly");

    }
}
