package primitives;

import org.junit.jupiter.api.Test;

public class Point2DTests {

    Point2D point2D = new Point2D(1, 2);

    @Test
    void testGetX() {
        assert point2D.getX() == 1;
    }

    @Test
    void testGetY() {
        assert point2D.getY() == 2;
    }

    @Test
    void testToString() {
        assert point2D.toString().equals("(1.0, 2.0)");
    }

}
