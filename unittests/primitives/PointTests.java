package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * unitest class {@link Point}
 * <p>
 * * The tests verify:
 * * <ul>
 * * <li>{@link Point#add(Vector)}</li>
 * * </ul>
 *
 * @author David & Yehuda
 */
class PointTests {

    PointTests() {
    }

    ;

    Point POINT = new Point(3, 4, 5);
    Vector VECTOR = new Vector(3, 4, 5);

    double DELTA = 1e-6;

    String ERROR_ADD = "ERROR: Point add(Vector) failed";

    /**
     * {@link Point#add(Vector)}
     */
    @Test
    void testAdd() {

        assertEquals(new Point(6, 8, 10), POINT.add(VECTOR),
                "ERROR: Point add(Vector) failed");

    }

}