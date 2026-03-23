package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VectorTests {

    /**
     * test method for {@link Vector#Vector(double, double, double)}.
     */
    @Test
    // Testing a constructor that receives three doubles
    void testConstructorA() {
        assertEquals(new Vector(1, 2, 3), new Vector(1, 2, 3),
                "ERROR: Vector constructor failed to create the expected vector");

    }

    /**
     * test method for {@link Vector#Vector(double, double, double)}.
     */

    @Test
    //check exception to zero vector
    void testConstructorB() {
        assertThrows(IllegalArgumentException.class, () -> new Vector(0, 0, 0),
                "ERROR: Vector constructor with zero vector should throw exception");
    }

    /**
     * test method for {@link Vector#add(Vector)}.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============

        // Testing the add method of the vector class
        assertEquals(new Vector(4, 5, 6), new Vector(1, 1, 1).add(new Vector(3, 4, 5)),
                "ERROR: Vector add(Vector) failed");

        // =============== Boundary Values Tests ==================

        //Testing the add method of the vector class in case negative values
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2, 3).add(new Vector(-1, -2, -3)),
                "ERROR: Vector add(Vector) with inverse vector should throw exception");
    }

    /**
     * test method for {@link Point#subtract(Point)}.
     */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============

        //Testing the subtract method of the vector class
        assertEquals(new Vector(3, 4, 5), new Vector(4, 5, 6).subtract(new Vector(1, 1, 1)),
                "ERROR: Vector subtract(Vector) failed");

        // =============== Boundary Values Tests ==================

        // Testing the subtract method of the vector class in case subtracting a vector from itself should throw an exception
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2, 3).subtract(new Vector(1, 2, 3)),
                "ERROR: Vector subtract(Vector) with itself should throw exception");
    }

    /**
     * test method for {@link Vector#scale(double)}.
     */
    @Test
    void testScale() {
        // ============ Equivalence Partitions Tests ==============

        //  Testing the scale method of the vector class with different factors
        assertEquals(new Vector(2, 4, 6), new Vector(1, 2, 3).scale(2),
                "ERROR: Vector scale(double) failed");

        //  Testing the scale method of the vector class with negative factor
        assertEquals(new Vector(-1, -2, -3), new Vector(1, 2, 3).scale(-1),
                "ERROR: Vector scale(double) with negative factor failed");

        //  Testing the scale method of the vector class with fractional factor
        assertEquals(new Vector(0.5, 1, 1.5), new Vector(1, 2, 3).scale(0.5),
                "ERROR: Vector scale(double) with fractional factor failed");

        // =============== Boundary Values Tests ==================

        // Testing the scale method of the vector class with zero factor should throw an exception
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2, 3).scale(0),
                "ERROR: Vector scale(double) with zero should throw exception");
    }

    /**
     * test method for {@link Vector#dotProduct(Vector)}.
     */
    @Test
    void testDotProduct() {
        // ============ Equivalence Partitions Tests ==============

        //  Testing the dotProduct method of the vector class with different vectors
        assertEquals(32, new Vector(1, 2, 3).dotProduct(new Vector(4, 5, 6)),
                "ERROR: Vector dotProduct(Vector) failed");

        //  Testing the dotProduct method of the vector class with negative vector
        assertEquals(-32, new Vector(1, 2, 3).dotProduct(new Vector(-4, -5, -6)),
                "ERROR: Vector dotProduct(Vector) with negative vector failed");

        // =============== Boundary Values Tests ==================

        //  Testing the dotProduct method of the vector class with orthogonal vectors should return zero
        assertEquals(0, new Vector(1, 0, 0).dotProduct(new Vector(0, 1, 0)), 0.00001,
                "ERROR: Vector dotProduct(Vector) with orthogonal vectors failed");
    }

    //  test method for {@link Vector#crossProduct(Vector)}.
    @Test
    void testCrossProduct() {
        // ============ Equivalence Partitions Tests ==============

        //  Testing the crossProduct method of the vector class with different vectors
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(4, 5, 6);
        Vector cross = v1.crossProduct(v2);
        assertEquals(new Vector(-3, 6, -3), cross,
                "ERROR: Vector crossProduct(Vector) failed");

        //  Testing the crossProduct method of the vector class with cross vector
        assertEquals(0, cross.dotProduct(v1), 0.00001,
                "ERROR: Vector crossProduct(Vector) is not orthogonal to the first vector");

        //  Testing the crossProduct method of the vector class with cross vector
        assertEquals(0, cross.dotProduct(v2), 0.00001,
                "ERROR: Vector crossProduct(Vector) is not orthogonal to the second vector");

        // =============== Boundary Values Tests ==================

        //  Testing the crossProduct method of the vector class with parallel vectors should throw an exception
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2, 3).crossProduct(new Vector(2, 4, 6)),
                "ERROR: Vector crossProduct(Vector) with parallel vector should throw exception");

        //  Testing the crossProduct method of the vector class with antiparallel vectors should throw an exception
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2, 3).crossProduct(new Vector(-2, -4, -6)),
                "ERROR: Vector crossProduct(Vector) with anti-parallel vector should throw exception");
    }

    //  test method for {@link Vector#lengthSquared()}.
    @Test
    void testLengthSquared() {
        // ============ Equivalence Partitions Tests ==============

        //  Testing the lengthSquared method of the vector class with different vectors
        assertEquals(14, new Vector(1, 2, 3).lengthSquared(),
                "ERROR: Vector lengthSquared() failed");

        //  Testing the lengthSquared method of the vector class with different vectors
        assertEquals(1, new Vector(0, 1, 0).lengthSquared(),
                "ERROR: Vector lengthSquared() with zero component failed");
    }

    //  test method for {@link Vector#length()}.
    @Test
    void testLength() {
        // ============ Equivalence Partitions Tests ==============

        //  Testing the length method of the vector class with different vectors
        assertEquals(3, new Vector(1, 2, 2).length(),
                "ERROR: Vector length() failed");

        //  Testing the length method of the vector class with different vectors
        assertEquals(Math.sqrt(50), new Vector(3, 4, 5).length(),
                "ERROR: Vector normalize() does not produce the expected normalized vector");

        //  Testing the length method of the vector class with different vectors
        assertEquals(1, new Vector(0, 1, 0).length(),
                "ERROR: Vector length() with zero component failed");
    }

    //  test method for {@link Vector#normalize()}.
    @Test
    void testNormalize() {
        // ============ Equivalence Partitions Tests ==============

        //  Testing the normalize method of the vector class with different vectors
        assertEquals(1, new Vector(1, 2, 3).normalize().length(), 0.00001,
                "ERROR: Vector normalize() does not produce a unit vector");

        //  Testing the normalize method of the vector class with different vectors
        assertEquals(1, new Vector(0.5, 0.35, 0.08).normalize().length(), 1.e-6,
                "ERROR: Vector normalize() does not produce the expected normalized vector");

        //
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2, 3).normalize().crossProduct(new Vector(1, 2, 3)),
                "ERROR: Vector normalize() with zero vector should throw exception");
    }
}
