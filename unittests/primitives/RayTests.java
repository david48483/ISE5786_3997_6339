package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * unit tests for {@link Ray} class
 * The tests verify:
 * <ul>
 * <li>Ray constructor validity</li>
 * <li>{@link Ray#origin()}</li>
 * <li>{@link Ray#direction()}</li>
 * </ul>
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 *
 * @author David
 * @author Yehuda
 */
class RayTests {

    /**
     * Vector (4,0,0) used in ray tests
     */
    private static final Vector vector1 = new Vector(4, 0, 0);

    /**
     * Vector (0,0,4) used in ray tests
     */

    private static final Vector vector2 = new Vector(0, 0, 4);

    /**
     * Point (1,2,3) used in ray tests
     */
    private static final Point point = new Point(1, 2, 3);

    private static final Ray ray1 = new Ray(point, vector1);

    /**
     * Test method for {@link Ray#Ray(Point, Vector)}.
     * Verifies correct ray construction and normalization of the direction vector.
     */

    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01 test for check constructor with valid inputs
        assertDoesNotThrow(() -> new Ray(point, vector1),
                "ERROR: Ray constructor failed to create the expected ray");
    }

    @Test
    void testDirection() {
        // ============ Equivalence Partitions Tests ==============

        //TC01 test for check return correct direction
        assertEquals(Vector.AXIS_X, ray1.direction(),
                "ERROR: Ray constructor failed to normalize the direction vector");
    }

    @Test
    void testOrigin() {
        // ============ Equivalence Partitions Tests ==============
        
        //TC01 test for check return correct origin
        assertEquals(point, ray1.origin(),
                "ERROR: Ray constructor failed to set the origin point correctly");

    }
}
