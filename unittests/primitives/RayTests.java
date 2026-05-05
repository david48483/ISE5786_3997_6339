package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
     * Default constructor for RayTests.
     */
    public RayTests() {
    }

    /**
     * Vector (4,0,0) used in ray tests.
     */
    private static final Vector vector1 = new Vector(4, 0, 0);


    /**
     * Point (1,2,3) used in ray tests
     */
    private static final Point point = new Point(1, 2, 3);

    /**
     * Ray created from point and vector1, used in ray tests
     */
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

    /**
     * Test method for {@link Ray#direction()}.
     * Verifies that the direction method returns the correct normalized direction vector.
     */
    @Test
    void testDirection() {
        // ============ Equivalence Partitions Tests ==============

        //TC01 test for check return correct direction
        assertEquals(Vector.AXIS_X, ray1.direction(),
                "ERROR: Ray constructor failed to normalize the direction vector");
    }

    /**
     * Test method for {@link Ray#origin()}.
     * Verifies that the origin method returns the correct origin point.
     */
    @Test
    void testOrigin() {
        // ============ Equivalence Partitions Tests ==============

        //TC01 test for check return correct origin
        assertEquals(point, ray1.origin(),
                "ERROR: Ray constructor failed to set the origin point correctly");

    }

    /**
     * * Test method for {@link Ray#getPoint(double)}.
     * Verifies that the getPoint method returns the correct point at a given distance along the ray.
     */
    @Test
    void testGetpoint(){
        // ============ Equivalence Partitions Tests ==============

        //EP01 test for check return correct point (t>0)
        assertEquals(new Point(3, 2, 3), ray1.getPoint(2),
                "ERROR: Ray getPoint() failed to return the correct point at distance t=2");

        //EP02 test for check return correct point (t<0)
        assertEquals(new Point(-1, 2, 3), ray1.getPoint(-2),
                "ERROR: Ray getPoint() failed to return the correct point at distance t=-2");

        // =============== Boundary Values Tests ==================

        assertThrows(IllegalArgumentException.class, () -> ray1.getPoint(0  ),
                "ERROR: Ray getPoint() with t=0 should throw exception");
    }
}
