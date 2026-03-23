package geometries;

import geometries.impl.Plane;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

public class PlaneTests {

    double DELTA = 1e-6;

    Point p2 = new Point(1, 0, 0);
    Point p3 = new Point(0, 1, 0);

    Vector v1 = new Vector(3, 4, 5);

    @Test
    void testConstructor() {

        // ============ Equivalence Partitions Tests ==============

        //TC01 check constructor
        assertDoesNotThrow(() -> new Plane(Point.ZERO, p2, p3),
                "ERORR,Plane constructor failed to create the expected plane  ");

        // =============== Boundary Values Tests ==================

        assertThrows(IllegalArgumentException.class, () -> new Plane(p2, p2, p3), "aaaa");

        assertThrows(IllegalArgumentException.class, () -> new Plane(p2, p3, p2), "aaaa");

        assertThrows(IllegalArgumentException.class, () -> new Plane(p3, p2, p2), "aaaa");

        assertThrows(IllegalArgumentException.class, () -> new Plane(p2, new Point(4, 0, 0), Point.ZERO),
                "aaaa");

    }

    @Test
    void testConstructurB() {
        Plane plane = new Plane(p2, v1);
        assertEquals(v1.normalize(), plane.getNormal(p2), "aaa");

    }

    @Test
    void testGetNormal() {

        Plane plane = new Plane(Point.ZERO, p2, p3);

        Vector expectedNormal = new Vector(0, 0, 1);

        // ============ Equivalence Partitions Tests ==============

        // TC01: A point that is not the reference
        assertEquals(expectedNormal, plane.getNormal(p2),
                "getNormal() wrong result for a point on the plane (not reference point)");

        // =============== Boundary Values Tests ==================

        // TC10: the reference point
        assertEquals(expectedNormal, plane.getNormal(Point.ZERO),
                "getNormal() wrong result for the reference point of the plane");
    }

}
