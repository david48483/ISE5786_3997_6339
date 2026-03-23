package geometries;

import geometries.impl.Triangle;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TriangleTests {

    @Test
    void testGetNormal() {
        // יצירת המשולש - יושב על מישור ה-XY
        Triangle triangle = new Triangle(new Point(4, 0, 0), new Point(0, 4, 0), Point.ZERO);

        // זה מה שאנחנו מצפים לקבל באמת מתמטית
        Vector expectedNormal = new Vector(0, 0, 1);

        // ============ Equivalence Partitions Tests ==============
        // TC01: נקודה בתוך המשולש
        assertEquals(expectedNormal, triangle.getNormal(new Point(1, 1, 0)),
                "ERROR: Triangle getNormal() returned wrong normal for a point inside the triangle");
    }
}