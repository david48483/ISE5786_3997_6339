package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VectorTests {

    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============
        assertEquals(new Vector(4, 5, 6), new Vector(1, 1, 1).add(new Vector(3, 4, 5)),
                "ERROR: Vector add(Vector) failed");

        // =============== Boundary Values Tests ==================
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2, 3).add(new Vector(-1, -2, -3)),
                "ERROR: Vector add(Vector) with inverse vector should throw exception");
    }

    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============
        assertEquals(new Vector(3, 4, 5), new Vector(4, 5, 6).subtract(new Vector(1, 1, 1)),
                "ERROR: Vector subtract(Vector) failed");

        // =============== Boundary Values Tests ==================
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2, 3).subtract(new Vector(1, 2, 3)),
                "ERROR: Vector subtract(Vector) with itself should throw exception");
    }

    @Test
    void testScale() {
        // ============ Equivalence Partitions Tests ==============
        assertEquals(new Vector(2, 4, 6), new Vector(1, 2, 3).scale(2),
                "ERROR: Vector scale(double) failed");

        assertEquals(new Vector(-1, -2, -3), new Vector(1, 2, 3).scale(-1),
                "ERROR: Vector scale(double) with negative factor failed");

        assertEquals(new Vector(0.5, 1, 1.5), new Vector(1, 2, 3).scale(0.5),
                "ERROR: Vector scale(double) with fractional factor failed");

        // =============== Boundary Values Tests ==================
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2, 3).scale(0),
                "ERROR: Vector scale(double) with zero should throw exception");
    }

    @Test
    void testDotProduct() {
        // ============ Equivalence Partitions Tests ==============
        assertEquals(32, new Vector(1, 2, 3).dotProduct(new Vector(4, 5, 6)),
                "ERROR: Vector dotProduct(Vector) failed");

        assertEquals(-32, new Vector(1, 2, 3).dotProduct(new Vector(-4, -5, -6)),
                "ERROR: Vector dotProduct(Vector) with negative vector failed");

        // =============== Boundary Values Tests ==================
        assertEquals(0, new Vector(1, 0, 0).dotProduct(new Vector(0, 1, 0)), 0.00001,
                "ERROR: Vector dotProduct(Vector) with orthogonal vectors failed");
    }

    @Test
    void testCrossProduct() {
        // ============ Equivalence Partitions Tests ==============
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(4, 5, 6);
        Vector cross = v1.crossProduct(v2);
        assertEquals(new Vector(-3, 6, -3), cross,
                "ERROR: Vector crossProduct(Vector) failed");
        assertEquals(0, cross.dotProduct(v1), 0.00001,
                "ERROR: Vector crossProduct(Vector) is not orthogonal to the first vector");

        assertEquals(0, cross.dotProduct(v2), 0.00001,
                "ERROR: Vector crossProduct(Vector) is not orthogonal to the second vector");

        // =============== Boundary Values Tests ==================
        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2, 3).crossProduct(new Vector(2, 4, 6)),
                "ERROR: Vector crossProduct(Vector) with parallel vector should throw exception");

        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2, 3).crossProduct(new Vector(-2, -4, -6)),
                "ERROR: Vector crossProduct(Vector) with anti-parallel vector should throw exception");
    }

    @Test
    void testLengthSquared() {
        // ============ Equivalence Partitions Tests ==============
        assertEquals(14, new Vector(1, 2, 3).lengthSquared(),
                "ERROR: Vector lengthSquared() failed");

        assertEquals(1, new Vector(0, 1, 0).lengthSquared(),
                "ERROR: Vector lengthSquared() with zero component failed");
    }

    @Test
    void testLength() {
        // ============ Equivalence Partitions Tests ==============
        assertEquals(3, new Vector(1, 2, 2).length(),
                "ERROR: Vector length() failed");

        assertEquals(1, new Vector(0, 1, 0).length(),
                "ERROR: Vector length() with zero component failed");
    }

    @Test
    void testNormalize() {
        assertEquals(1, new Vector(1, 2, 3).normalize().length(), 0.00001,
                "ERROR: Vector normalize() does not produce a unit vector");

        assertThrows(IllegalArgumentException.class, () -> new Vector(1, 2, 3).normalize().crossProduct(new Vector(1, 2, 3)),
                "ERROR: Vector normalize() with zero vector should throw exception");
    }
}
