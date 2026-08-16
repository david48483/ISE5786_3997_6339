package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AABB} class.
 * The tests verify:
 * <ul>
 *   <li>Box creation and min/max accessors</li>
 *   <li>Fast boolean {@link AABB#intersects(Ray, double)} across EP/BVA</li>
 *   <li>{@link AABB#union(AABB)} correctness</li>
 * </ul>
 * Tests follow Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class AABBTests {

    /**
     * Shared box used across tests as a thin area on the XY plane.
     */
    private static final AABB BOX_XY = new AABB(new Point(0, 0, 0), new Point(4, 4, 0));

    /**
     * Error message for invalid minimum corner assertions.
     */
    private static final String ERR_BOX_MIN = "ERROR: Bounding box min point is incorrect";
    /**
     * Error message for invalid maximum corner assertions.
     */
    private static final String ERR_BOX_MAX = "ERROR: Bounding box max point is incorrect";
    /**
     * Error message for invalid intersection assertions.
     */
    private static final String ERR_INTERSECTS = "ERROR: AABB.intersects() returned wrong result";
    /**
     * Error message for invalid union assertions.
     */
    private static final String ERR_UNION = "ERROR: AABB.union() returned wrong box";

    /**
     * Creates the test suite instance.
     */
    AABBTests() {
    }

    /**
     * Test method for basic box creation and accessors.
     */
    @Test
    void testBoxCreation() {
        assertEquals(new Point(0, 0, 0), BOX_XY.getMin(), ERR_BOX_MIN);
        assertEquals(new Point(4, 4, 0), BOX_XY.getMax(), ERR_BOX_MAX);
    }

    /**
     * Tests boolean ray-box intersection across EP and BVA.
     */
    @Test
    void testIntersects() {
        double INF = Double.POSITIVE_INFINITY;

        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray starts outside and hits the box (through z = -10 toward +Z)
        assertTrue(BOX_XY.intersects(new Ray(new Point(2, 2, -10), Vector.AXIS_Z), INF), ERR_INTERSECTS);

        // EP02: Ray misses the box entirely (y is out of [0,4])
        assertFalse(BOX_XY.intersects(new Ray(new Point(2, 10, -10), Vector.AXIS_Z), INF), ERR_INTERSECTS);

        // EP03: Ray would hit, but the intersection is beyond maxDistance (entry at t=10, limit 9)
        assertFalse(BOX_XY.intersects(new Ray(new Point(2, 2, -10), Vector.AXIS_Z), 9.0), ERR_INTERSECTS);

        // EP04: Ray starts outside but goes away from the box (z=10, direction +Z)
        assertFalse(BOX_XY.intersects(new Ray(new Point(2, 2, 10), Vector.AXIS_Z), INF), ERR_INTERSECTS);

        // ============ Boundary Values Tests ==================

        // BVA01: Ray starts strictly INSIDE the box
        assertTrue(BOX_XY.intersects(new Ray(new Point(1, 1, 0), Vector.AXIS_Z), INF), ERR_INTERSECTS);

        // BVA02: Ray starts ON the boundary plane (z = 0) pointing outwards; t=0 is valid
        assertTrue(BOX_XY.intersects(new Ray(new Point(2, 2, 0), Vector.AXIS_Z), INF), ERR_INTERSECTS);

        // BVA03: Ray aligned with a box plane (zero direction on Z), sliding along X on the top plane
        assertTrue(BOX_XY.intersects(new Ray(new Point(2, 2, 0), Vector.AXIS_X), INF), ERR_INTERSECTS);

        // BVA04: Ray parallel just outside the box (x slightly out of range)
        assertFalse(BOX_XY.intersects(new Ray(new Point(4.1, 2, -10), Vector.AXIS_Z), INF), ERR_INTERSECTS);

        // BVA05: Ray starts inside with maxDistance exactly 0 -> intersection at t=0 is allowed
        assertTrue(BOX_XY.intersects(new Ray(new Point(2, 2, 0), Vector.AXIS_Z), 0.0), ERR_INTERSECTS);

        // BVA06: Entry exactly at the distance limit (entry t = 10)
        assertTrue(BOX_XY.intersects(new Ray(new Point(2, 2, -10), Vector.AXIS_Z), 10.0), ERR_INTERSECTS);
    }

    /**
     * Tests union of two boxes and union with null.
     */
    @Test
    void testUnion() {
        AABB b1 = new AABB(new Point(0, 0, 0), new Point(4, 4, 0));
        AABB b2 = new AABB(new Point(-1, 1, -2), new Point(2, 5, 1));

        // Union with non-null
        AABB u = b1.union(b2);
        assertEquals(new Point(-1, 0, -2), u.getMin(), ERR_UNION);
        assertEquals(new Point(4, 5, 1), u.getMax(), ERR_UNION);

        // Union with null -> should return the same box (b1)
        assertSame(b1, b1.union(null), ERR_UNION);
    }
}
