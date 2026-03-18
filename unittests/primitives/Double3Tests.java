package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for primitives.Double3 class
 * * The tests verify:
 * * <ul>
 * * <li>Constructor {@link Polygon#Polygon(Point...)}</li>
 * * <li>{@link Polygon#getNormal(Point)}</li>
 * * </ul>
 * <p>
 * <p>
 * * @author David & yehuda
 */
class Double3Tests {

    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 0.000001;

    /**
     * Error message for arithmetic operations
     */
    private static final String ERROR_MATH = "Math operation failed";

    /**
     * Test method for {@link primitives.Double3#add(primitives.Double3)}.
     */
    @Test
    void testAdd() {
        Double3 d1 = new Double3(1.0, 2.0, 3.0);
        Double3 d2 = new Double3(2.0, 4.0, 6.0);
        Double3 d3 = new Double3(-1.0, -2.0, -3.0);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple addition of two Double3 objects
        assertEquals(new Double3(3.0, 6.0, 9.0), d1.add(d2),
                ERROR_MATH);

        // EP02: Addition of positive and negative Double3 objects
        assertEquals(Double3.ZERO, d1.add(d3),
                ERROR_MATH);

        // =============== Boundary Values Tests ==================
        // BV01: Addition with ZERO triad
        assertEquals(d1, d1.add(Double3.ZERO),
                ERROR_MATH);
    }
}