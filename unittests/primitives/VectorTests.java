package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link Vector}.
 * The tests verify:
 * <ul>
 *     <li>{@link Vector#Vector(double, double, double)}</li>
 *     <li>{@link Vector#Vector(Double3)}</li>
 *     <li>{@link Vector#add(Vector)}</li>
 *     <li>{@link Point#subtract(Point)}</li>
 *     <li>{@link Vector#scale(double)}</li>
 *     <li>{@link Vector#dotProduct(Vector)}</li>
 *     <li>{@link Vector#crossProduct(Vector)}</li>
 *     <li>{@link Vector#lengthSquared()}</li>
 *     <li>{@link Vector#length()}</li>
 *     <li>{@link Vector#normalize()}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 *
 * @author David &amp; Yehuda
 */
public class VectorTests {

    /**
     * Default constructor for VectorTests.
     */
    public VectorTests() {
    }

    // ---- Error messages ----

    /** Error message for {@link Vector#Vector(double, double, double)} tests. */
    private static final String ERR_CONSTRUCTOR   = "ERROR: Vector constructor failed";

    /** Error message for {@link Vector#add(Vector)} tests. */
    private static final String ERR_ADD           = "ERROR: Vector add(Vector) failed";

    /** Error message for {@link Point#subtract(Point)} tests. */
    private static final String ERR_SUBTRACT      = "ERROR: Vector subtract(Vector) failed";

    /** Error message for {@link Vector#scale(double)} tests. */
    private static final String ERR_SCALE         = "ERROR: Vector scale(double) failed";

    /** Error message for {@link Vector#dotProduct(Vector)} tests. */
    private static final String ERR_DOT_PRODUCT   = "ERROR: Vector dotProduct(Vector) failed";

    /** Error message for {@link Vector#crossProduct(Vector)} tests. */
    private static final String ERR_CROSS_PRODUCT = "ERROR: Vector crossProduct(Vector) failed";

    /** Error message for {@link Vector#lengthSquared()} tests. */
    private static final String ERR_LENGTH_SQ     = "ERROR: Vector lengthSquared() failed";

    /** Error message for {@link Vector#length()} tests. */
    private static final String ERR_LENGTH        = "ERROR: Vector length() failed";

    /** Error message for {@link Vector#normalize()} tests. */
    private static final String ERR_NORMALIZE     = "ERROR: Vector normalize() failed";

    // ---- Numeric precision ----

    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    // ---- Shared vectors ----

    /**
     * Vector (1,2,3) — primary test vector.
     */
    private static final Vector V1 = new Vector(1, 2, 3);

    /**
     * Vector (4,5,6) — secondary test vector; equals V1 + V3.
     */
    private static final Vector V2 = new Vector(4, 5, 6);

    /**
     * Vector (3,3,3) — used to verify V1 + V3 = V2.
     */
    private static final Vector V3 = new Vector(3, 3, 3);

    /**
     * Vector (3,4,0) — has length 5, used in length tests.
     */
    private static final Vector V4 = new Vector(3, 4, 0);

    /**
     * Negation of V1: (−1,−2,−3) — used as inverse in add/crossProduct BVA tests.
     */
    private static final Vector V1_NEG = new Vector(-1, -2, -3);

    /**
     * V1 scaled by 2: (2,4,6) — expected result of scale(2); parallel to V1.
     */
    private static final Vector V1_DOUBLE = new Vector(2, 4, 6);

    /**
     * V1 scaled by −2: (−2,−4,−6) — anti-parallel to V1, used in crossProduct BVA.
     */
    private static final Vector V1_NEG_DOUBLE = new Vector(-2, -4, -6);

    /**
     * Negation of V2: (−4,−5,−6) — used in dotProduct EP test (obtuse angle).
     */
    private static final Vector V2_NEG = new Vector(-4, -5, -6);

    /**
     * Vector (−4, 0.5, 1) — orthogonal to V1, used in dotProduct BVA test.
     */
    private static final Vector V1_ORTHO = new Vector(-4, 0.5, 1);

    /**
     * Vector (0,3,4) — lies in the YZ plane, has length 5.
     */
    private static final Vector V_YZ = new Vector(0, 3, 4);

    /**
     * Vector (0,−3,−4) — negation of V_YZ, has length 5.
     */
    private static final Vector V_YZ_NEG = new Vector(0, -3, -4);

    // ---- Tests ----

    /**
     * Test method for {@link Vector#Vector(double, double, double)} and
     * {@link Vector#Vector(Double3)}.
     */
    @Test
    void testConstructor() {

        // ============ Equivalence Partitions Tests ==============

        // EP01: Valid non-zero components — must not throw
        assertDoesNotThrow(() -> new Vector(1, 2, 3), ERR_CONSTRUCTOR);

        // =============== Boundary Values Tests ==================

        // BVA11: Zero vector — must throw
        assertThrows(IllegalArgumentException.class, () -> new Vector(Double3.ZERO), ERR_CONSTRUCTOR);
    }

    /**
     * Test method for {@link Vector#add(Vector)}.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Sum of two non-zero vectors
        assertEquals(V2, V1.add(V3), ERR_ADD);

        // =============== Boundary Values Tests ==================

        // BVA11: Adding inverse vector produces zero vector — must throw
        assertThrows(IllegalArgumentException.class, () -> V1.add(V1_NEG), ERR_ADD);
    }

    /**
     * Test method for {@link Point#subtract(Point)}.
     */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Difference of two different vectors
        assertEquals(V1, V2.subtract(V3), ERR_SUBTRACT);

        // =============== Boundary Values Tests ==================

        // BVA11: Subtracting a vector from itself produces zero vector — must throw
        assertThrows(IllegalArgumentException.class, () -> V1.subtract(V1), ERR_SUBTRACT);
    }

    /**
     * Test method for {@link Vector#scale(double)}.
     */
    @Test
    void testScale() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Scale by positive factor
        assertEquals(V1_DOUBLE, V1.scale(2), ERR_SCALE);

        // =============== Boundary Values Tests ==================

        // BVA11: Scale by zero produces zero vector — must throw
        assertThrows(IllegalArgumentException.class, () -> V1.scale(0), ERR_SCALE);
    }

    /**
     * Test method for {@link Vector#dotProduct(Vector)}.
     */
    @Test
    void testDotProduct() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Acute angle — positive dot product
        assertEquals(32, V1.dotProduct(V2), DELTA, ERR_DOT_PRODUCT);

        // EP02: Obtuse angle — negative dot product
        assertEquals(-32, V1.dotProduct(V2_NEG), DELTA, ERR_DOT_PRODUCT);

        // =============== Boundary Values Tests ==================

        // BVA11: Orthogonal vectors — dot product must be zero
        assertEquals(0, V1.dotProduct(V1_ORTHO), DELTA, ERR_DOT_PRODUCT);
    }

    /**
     * Test method for {@link Vector#crossProduct(Vector)}.
     */
    @Test
    void testCrossProduct() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Cross product of two non-parallel vectors — correct result vector
        Vector cross = V1.crossProduct(V2);
        assertEquals(new Vector(-3, 6, -3), cross, ERR_CROSS_PRODUCT);

        // EP02: Result must be orthogonal to both operands
        assertEquals(0, cross.dotProduct(V1), DELTA, ERR_CROSS_PRODUCT);
        assertEquals(0, cross.dotProduct(V2), DELTA, ERR_CROSS_PRODUCT);

        // =============== Boundary Values Tests ==================

        // BVA11: Parallel vectors — cross product is zero vector, must throw
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(V1_NEG_DOUBLE), ERR_CROSS_PRODUCT);
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(V1_NEG),        ERR_CROSS_PRODUCT);
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(V1_DOUBLE),     ERR_CROSS_PRODUCT);
    }

    /**
     * Test method for {@link Vector#lengthSquared()}.
     */
    @Test
    void testLengthSquared() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: General non-unit vector
        assertEquals(14, V1.lengthSquared(), DELTA, ERR_LENGTH_SQ);

        // =============== Boundary Values Tests ==================

        // BVA11: Unit vector — squared length must equal 1
        assertEquals(1, Vector.AXIS_X.lengthSquared(), DELTA, ERR_LENGTH_SQ);
    }

    /**
     * Test method for {@link Vector#length()}.
     */
    @Test
    void testLength() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Vectors with integer length 5 in different planes
        assertEquals(5, V4.length(),       DELTA, ERR_LENGTH);
        assertEquals(5, V_YZ.length(),     DELTA, ERR_LENGTH);
        assertEquals(5, V_YZ_NEG.length(), DELTA, ERR_LENGTH);

        // =============== Boundary Values Tests ==================

        // BVA11: Unit vector — length must equal 1
        assertEquals(1, Vector.AXIS_Y.length(), DELTA, ERR_LENGTH);
    }

    /**
     * Test method for {@link Vector#normalize()}.
     */
    @Test
    void testNormalize() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Test normal vector normalization
        Vector normalizedVector = V1.normalize();

        // Result must be a unit vector (length equals 1)
        assertEquals(1, normalizedVector.length(), DELTA, ERR_NORMALIZE);

        // Normalized vector is parallel to the original — cross product must be zero (throws exception for zero-vector)
        assertThrows(IllegalArgumentException.class, () -> normalizedVector.crossProduct(V1), ERR_NORMALIZE);

        // Normalized vector must point in the same direction as the original (dot product is positive)
        assertTrue(normalizedVector.dotProduct(V1) > 0, ERR_NORMALIZE);

        // =============== Boundary Values Tests ==================

        // BVA01: Normalizing an already-unit vector returns an equivalent unit vector
        assertEquals(Vector.AXIS_X, Vector.AXIS_X.normalize(), ERR_NORMALIZE);
    }
}
