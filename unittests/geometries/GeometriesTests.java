package geometries;

import geometries.impl.Geometries;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeometriesTests {
    //

    Plane plane = new Plane(new Point(0, 0, 1), new Point(1, 0, 1), new Point(0, 1, 1));

    Sphere sphere = new Sphere(new Point(0, 0, 0), 5);

    Triangle triangle11 = new Triangle(new Point(0, 0, 0), new Point(1, 0, 0), new Point(0, 1, 0));

    Triangle triangle2 = new Triangle(new Point(0, 0, 1), new Point(1, 0, 1), new Point(0, 1, 1));

    Plane plane1 = new Plane(Point.ZERO, Vector.AXIS_Z);

    Sphere sphere1 = new Sphere(Point.ZERO, 5);

    Triangle tringle1 = new Triangle(Point.ZERO, new Point(4, 0, 0), new Point(0, 4, 0));

    //ary that intersects all three geometries
    Ray ray1 = new Ray(new Point(1, 1, -1), Vector.AXIS_Z);

    @Test
    void testConstructor() {

        assertDoesNotThrow(() -> new Geometries(plane, sphere, triangle11),
                "ERROR: Geometries constructor threw an exception when given valid geometries");

    }

    @Test
    void testAdd() {
        assertDoesNotThrow(() -> {
            Geometries geometries = new Geometries(plane, sphere);
            
            geometries.add(triangle11, triangle2);
        }, "ERROR: Geometries add() method threw an exception when adding valid geometries");

    }

    @Test
    void testFindIntersections() {
        Geometries geometries = new Geometries(plane1, sphere1, tringle1);

        assertEquals(3, geometries.findIntersections(ray1).size(),
                "ERROR: Geometries findIntersections() method returned wrong number of intersections");

    }

}
