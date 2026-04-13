package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VectorTests {
    //  Delta value for accuracy when comparing double values.
    private static final double DELTA = 1e-6;

    //  Vectors used in the tests
    private static final Vector V1 = new Vector(1, 2, 3);

    //  Another vector used in the tests
    private static final Vector V2 = new Vector(4, 5, 6);

    //  A vector with all components equal to 3, used in the tests
    private static final Vector V3 = new Vector(3, 3, 3);

    private static final Vector V4 = new Vector(3, 4, 0);

    /**
     * test method for {@link Vector#Vector(double, double, double)}.
     * test method for {@link Vector#Vector(Double3)}.
     */
    @Test
    void testConstructor() {

        // ============ Equivalence Partitions Tests ==============

        //TC01 Testing a constructor that receives three doubles
        assertDoesNotThrow(() -> new Vector(1, 2, 3),
                "ERROR: Vector constructor with valid inputs should not throw exception");

        // =============== Boundary Values Tests ==================

        //TC11 check exception to zero vector
        assertThrows(IllegalArgumentException.class, () -> new Vector(Double3.ZERO),
                "ERROR: Vector constructor with zero vector should throw exception");
    }

    /**
     * test method for {@link Vector#add(Vector)}.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============

        //TC01 Testing the add method of the vector class
        assertEquals(V2, V1.add(V3),
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
        assertEquals(V1, V2.subtract(V3),
                "ERROR: Vector subtract(Vector) failed");

        // =============== Boundary Values Tests ==================

        //TC11 Testing the subtract method of the vector class in case subtracting a vector from itself should throw an exception
        assertThrows(IllegalArgumentException.class, () -> V1.subtract(V1),
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

        //TC01  Testing the dotProduct method of the vector class with different vectors, acute angle
        assertEquals(32, V1.dotProduct(V2), DELTA,
                "ERROR: Vector dotProduct(Vector) failed");

        //TC02  Testing the dotProduct method of the vector class with negative vector, obtuse angle
        assertEquals(-32, V1.dotProduct(new Vector(-4, -5, -6)), DELTA,
                "ERROR: Vector dotProduct(Vector) with negative vector failed");

        // =============== Boundary Values Tests ==================

        //TC11  Testing the dotProduct method of the vector class with orthogonal vectors should return zero
        assertEquals(0, V1.dotProduct(new Vector(-4, 0.5, 1)), DELTA,
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
        assertEquals(0, cross.dotProduct(V1), DELTA,
                "ERROR: Vector crossProduct(Vector) is not orthogonal to the first vector");

        assertEquals(0, cross.dotProduct(V2), DELTA,
                "ERROR: Vector crossProduct(Vector) is not orthogonal to the second vector");

        // =============== Boundary Values Tests ==================

        // TC11 Testing the crossProduct method of the vector class with parallel vectors should throw an exception
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(new Vector(-2, -4, -6)),
                "ERROR: Vector crossProduct(Vector) with parallel vector should throw exception");

        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(new Vector(-1, -2, -3)),
                "ERROR: Vector crossProduct(Vector) with parallel vector should throw exception");

        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(new Vector(2, 4, 6)),
                "ERROR: Vector crossProduct(Vector) with parallel vector should throw exception");
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

        //===== Boundary Values Tests ==================

        // TC11 Testing the lengthSquared method of the vector class with zero component
        assertEquals(1, Vector.AXIS_X.lengthSquared(), DELTA,
                "ERROR: Vector lengthSquared() with zero component failed");
    }

    /**
     * test method for {@link Vector#length()}.
     */

    @Test
    void testLength() {
        // ============ Equivalence Partitions Tests ==============

        // TC01 Testing the length method of the vector class with different vectors
        assertEquals(5, V4.length(), DELTA,
                "ERROR: Vector length() failed");

        assertEquals(5, new Vector(0, 3, 4).length(), DELTA,
                "ERROR: Vector length() failed");

        assertEquals(5, new Vector(0, -3, -4).length(), DELTA,
                "ERROR: Vector length() failed");

        //==Boundary Values Tests ==================

        //  TC11 Testing the length method of the vector class with zero component
        assertEquals(1, new Vector(0, 1, 0).length(), DELTA,
                "ERROR: Vector length() with zero component failed");
    }

    /**
     * test method for {@link Vector#normalize()}.
     */

    @Test
    void testNormalize() {
        // ============ Equivalence Partitions Tests ==============

        Vector normlizeVector = V1.normalize();
        //TC01  Testing the normalize method of the vector class with different vectors
        assertEquals(1, normlizeVector.length(), DELTA,
                "ERROR: Vector normalize() does not produce a unit vector");

        //TC02  Testing the normalize method of the vector class with different vectors
        assertThrows(IllegalArgumentException.class, () -> normlizeVector.crossProduct(V1),
                "ERROR: Vector normalize() does not produce a vector orthogonal to the original vector");

        //check vector if is same direction as the original vector
        assertTrue(normlizeVector.dotProduct(V1) > 0,
                "ERROR: Vector normalize() does not produce a vector in the same direction as the original vector");

        //=== Boundary Values Tests ==================

        // TC11 Testing the normalize method of the vector class with zero component should throw an exception
        assertEquals(Vector.AXIS_X, Vector.AXIS_X.normalize().length(),
                "ERROR: vector normlize has not changed the vector");
    }
}
