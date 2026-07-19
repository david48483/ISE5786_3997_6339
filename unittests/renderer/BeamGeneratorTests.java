package renderer;

import org.junit.jupiter.api.Test;
import primitives.Point2D;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeamGeneratorTests {

    @Test
    public void testGenerateGrid() {
        BeamGenerator generator = new BeamGenerator().setUseJitter(false);

        // נבקש רשת של 2x2 בתוך אזור בגודל 1.0
        // הנקודות אמורות להיות ב: (-0.5, -0.5), (-0.5, 0.5), (0.5, -0.5), (0.5, 0.5)
        List<Point2D> points = generator.generateGrid(2, 1.0);

        // בדיקה שיש לנו בדיוק 4 נקודות
        assertEquals(4, points.size(), "Grid 2x2 should produce 4 points");

        // בדיקה שהערכים אכן נכונים (משתמשים ב-delta קטן בגלל דיוק של double)
        double delta = 1e-10;

        // הנה הנקודות שאנחנו מצפים לקבל (הסדר תלוי בלולאות, אצלי זה X רץ חיצוני, Y פנימי)
        assertTrue(isPointInList(points, -0.5, -0.5, delta));
        assertTrue(isPointInList(points, -0.5, 0.5, delta));
        assertTrue(isPointInList(points, 0.5, -0.5, delta));
        assertTrue(isPointInList(points, 0.5, 0.5, delta));
    }

    // פונקציית עזר לבדיקה שנקודה קיימת ברשימה
    private boolean isPointInList(List<Point2D> points, double x, double y, double delta) {
        for (Point2D p : points) {
            if (Math.abs(p.getX() - x) < delta && Math.abs(p.getY() - y) < delta) {
                return true;
            }
        }
        return false;
    }
}