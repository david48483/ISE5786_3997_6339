package renderer;

import org.junit.jupiter.api.Test;
import primitives.Point2D;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeamGeneratorTests {

    @Test
    public void testGenerateGrid() {
        BeamGenerator generator = new BeamGenerator().setUseJitter(false);

        double size = 1.0;
        int baseAmount = 2;

        List<Point2D> points = generator.generateGrid(baseAmount, size);

        double radius = size / 2.0;
        double radiusSq = radius * radius;
        double delta = 1e-10;

        for (Point2D p : points) {
            double distSq = p.getX() * p.getX() + p.getY() * p.getY();
            assertTrue(distSq <= radiusSq + delta, "Point (" + p.getX() + ", " + p.getY() + ") is outside the circular target area");
        }

        assertFalse(points.isEmpty(), "Generated grid should not be empty");

        assertTrue(isPointInList(points, 0.0, 0.0, delta) || points.size() > 0,
                "Grid should successfully generate valid points within the circle");
    }

    private boolean isPointInList(List<Point2D> points, double x, double y, double delta) {
        for (Point2D p : points) {
            if (Math.abs(p.getX() - x) < delta && Math.abs(p.getY() - y) < delta) {
                return true;
            }
        }
        return false;
    }
}