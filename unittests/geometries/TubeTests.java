package geometries;

import geometries.impl.Tube;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * unit tests for {@link Tube} class
 * <ul>
 * <li>{@link Tube#getNormal(Point)} </li>
 * </ul>
 *  Tests follow the methodology of
 *  Equivalence Partitions (EP) and Boundary Values (BVA).
 *
 * @author David & Yheuda
 *
 */

public class TubeTests {

    /**
     * Test method for {@link Tube#getNormal(Point)}.
     * check that the normal vector is correct for points on the tube, including points on the boundary of the tube.
     */

    @Test
    void testGetNormal() {

        //  create a simple tube
        Tube tube = new Tube(1, new Ray(Point.ZERO, Vector.AXIS_Z));

        //  this is the expected normal vector for points on the tube
        Vector expectedNormal = new Vector(1, 0, 0);

        // ============ Equivalence Partitions Tests ==============

        //TC01 a regular check
        assertEquals(expectedNormal, tube.getNormal(new Point(1, 0, 5)),
                "ERROR: Tube getNormal() returned wrong normal for a regular point on the tube");

        //TC02 check for point on the tube with negative z value
        assertEquals(expectedNormal, tube.getNormal(new Point(1, 0, -5)),
                "ERROR: Tube getNormal() returned wrong normal for a point on the tube behind the axis");

        // ============ Boundary Values Tests ==============

        //TC11 check for point on the tube with z value of 0
        assertEquals(expectedNormal, tube.getNormal(new Point(1, 0, 0)),
                "ERROR: Tube getNormal() returned wrong normal for a point on the tube opposite the axis");
    }

}
