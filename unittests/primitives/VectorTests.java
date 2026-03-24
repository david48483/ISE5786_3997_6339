package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VectorTests {
    //  Delta value for accuracy when comparing double values.
    private static final double DELTA = 1e-6;

    //  Vectors used in the tests
    private static final Vector V1 = new Vector(1, 2, 3);

    //  Another vector used in the tests
    private static final Vector V2 = new Vector(4, 5, 6);

    /**
     * test method for {@link Vector#Vector(double, double, double)}.
     */
    @Test
    void testConstructorA() {

        //TC01 Testing a constructor that receives three doubles
        assertEquals(V1, V1,
                "ERROR: Vector constructor failed to create the expected vector");

    }

    /**
     * test method for {@link Vector#Vector(double, double, double)}.
     */

    @Test
    void testConstructorB() {

        //TC01 check exception to zero vector
        assertThrows(IllegalArgumentException.class, () -> new Vector(0, 0, 0),
                "ERROR: Vector constructor with zero vector should throw exception");
    }

    /**
     * test method for {@link Vector#add(Vector)}.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============

        //TC01 Testing the add method of the vector class
        assertEquals(V2, new Vector(1, 1, 1).add(new Vector(3, 4, 5)),
                "ERROR: Vector add(Vector) failed");

        // =============== Boundary Values Tests ==================

        //TC11 Testing the add method of the vector class in case negative values
        assertThrows(IllegalArgumentException.class, () -> V1.add(new Vector(-1, -2, -3)),
                "ERROR: Vector add(Vector) with inverse vector should throw exception");
    }

    /**
     * test method for {@link Point#subtract(Point)}.
     */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============

        //TC01 Testing the subtract method of the vector class
        assertEquals(new Vector(3, 4, 5), V2.subtract(new Vector(1, 1, 1)),
                "ERROR: Vector subtract(Vector) failed");

        // =============== Boundary Values Tests ==================

        //TC11 Testing the subtract method of the vector class in case subtracting a vector from itself should throw an exception
        assertThrows(IllegalArgumentException.class, () -> V1.subtract(new Vector(1, 2, 3)),
                "ERROR: Vector subtract(Vector) with itself should throw exception");
    }

    /**
     * test method for {@link Vector#scale(double)}.
     */
    @Test
    void testScale() {
        // ============ Equivalence Partitions Tests ==============

        //TC01  Testing the scale method of the vector class with different factors
        assertEquals(new Vector(2, 4, 6), V1.scale(2),
                "ERROR: Vector scale(double) failed");

        // TC02 Testing the scale method of the vector class with negative factor
        assertEquals(new Vector(-1, -2, -3), V1.scale(-1),
                "ERROR: Vector scale(double) with negative factor failed");

        //TC03  Testing the scale method of the vector class with fractional factor
        assertEquals(new Vector(0.5, 1, 1.5), V1.scale(0.5),
                "ERROR: Vector scale(double) with fractional factor failed");

        // =============== Boundary Values Tests ==================

        //TC11 Testing the scale method of the vector class with zero factor should throw an exception
        assertThrows(IllegalArgumentException.class, () -> V1.scale(0),
                "ERROR: Vector scale(double) with zero should throw exception");
    }

    /**
     * test method for {@link Vector#dotProduct(Vector)}.
     */
    @Test
    void testDotProduct() {
        // ============ Equivalence Partitions Tests ==============

        //TC01  Testing the dotProduct method of the vector class with different vectors
        assertEquals(32, V1.dotProduct(V2), DELTA,
                "ERROR: Vector dotProduct(Vector) failed");

        //TC02  Testing the dotProduct method of the vector class with negative vector
        assertEquals(-32, V1.dotProduct(new Vector(-4, -5, -6)), DELTA,
                "ERROR: Vector dotProduct(Vector) with negative vector failed");

        // =============== Boundary Values Tests ==================

        //TC11  Testing the dotProduct method of the vector class with orthogonal vectors should return zero
        assertEquals(0, new Vector(1, 0, 0).dotProduct(new Vector(0, 1, 0)), 0.00001,
                "ERROR: Vector dotProduct(Vector) with orthogonal vectors failed");
    }

    /**
     * test method for {@link Vector#crossProduct(Vector)}.
     */

    @Test
    void testCrossProduct() {
        // ============ Equivalence Partitions Tests ==============

        //TC01  Testing the crossProduct method of the vector class with different vectors
        Vector cross = V1.crossProduct(V2);
        assertEquals(new Vector(-3, 6, -3), cross,
                "ERROR: Vector crossProduct(Vector) failed");

        // TC02 Testing the crossProduct method of the vector class with cross vector
        assertEquals(0, cross.dotProduct(V1), 0.00001,
                "ERROR: Vector crossProduct(Vector) is not orthogonal to the first vector");

        //TC03  Testing the crossProduct method of the vector class with cross vector
        assertEquals(0, cross.dotProduct(V2), 0.00001,
                "ERROR: Vector crossProduct(Vector) is not orthogonal to the second vector");

        // =============== Boundary Values Tests ==================

        // TC11 Testing the crossProduct method of the vector class with parallel vectors should throw an exception
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(new Vector(2, 4, 6)),
                "ERROR: Vector crossProduct(Vector) with parallel vector should throw exception");

        //TC12  Testing the crossProduct method of the vector class with antiparallel vectors should throw an exception
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(new Vector(-2, -4, -6)),
                "ERROR: Vector crossProduct(Vector) with anti-parallel vector should throw exception");
    }

    /**
     * test method for {@link Vector#lengthSquared()}.
     */

    @Test
    void testLengthSquared() {
        // ============ Equivalence Partitions Tests ==============

        //TC01  Testing the lengthSquared method of the vector class with different vectors
        assertEquals(14, V1.lengthSquared(), DELTA,
                "ERROR: Vector lengthSquared() failed");

        //TC02  Testing the lengthSquared method of the vector class with different vectors
        assertEquals(1, new Vector(0, 1, 0).lengthSquared(), DELTA,
                "ERROR: Vector lengthSquared() with zero component failed");
    }

    /**
     * test method for {@link Vector#length()}.
     */

    @Test
    void testLength() {
        // ============ Equivalence Partitions Tests ==============

        // TC01 Testing the length method of the vector class with different vectors
        assertEquals(3, new Vector(1, 2, 2).length(), DELTA,
                "ERROR: Vector length() failed");

        //TC02  Testing the length method of the vector class with different vectors
        assertEquals(Math.sqrt(50), new Vector(3, 4, 5).length(), DELTA,
                "ERROR: Vector length() with an incomplete result  failed");

        //TC03  Testing the length method of the vector class with different vectors
        assertEquals(1, new Vector(0, 1, 0).length(), DELTA,
                "ERROR: Vector length() with zero component failed");
    }

    /**
     * test method for {@link Vector#normalize()}.
     */

    @Test
    void testNormalize() {
        // ============ Equivalence Partitions Tests ==============

        //TC01  Testing the normalize method of the vector class with different vectors
        assertEquals(1, V1.normalize().length(), 0.00001,
                "ERROR: Vector normalize() does not produce a unit vector");

        // TC02 Testing the normalize method of the vector class with different vectors
        assertEquals(1, new Vector(0.5, 0.35, 0.08).normalize().length(), 1.e-6,
                "ERROR: Vector normalize() does not produce the expected normalized vector");

        //TC03  Testing the normalize method of the vector class with different vectors
        assertThrows(IllegalArgumentException.class, () -> V1.normalize().crossProduct(V1),
                "ERROR: Vector normalize() does not produce a vector orthogonal to the original vector");
    }
}
