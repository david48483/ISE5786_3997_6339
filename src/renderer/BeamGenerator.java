package renderer;

import primitives.Point;
import primitives.Point2D;
import primitives.Ray;
import primitives.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BeamGenerator {

    // אובייקט רנדום לייצור ה"רעש" (Jittering) כדי למנוע תבניות לא טבעיות
    private static final Random RANDOM = new Random();

    private boolean useJitter = true; // כברירת מחדל זה דלוק בשביל הרינדור

    // פונקציית הגדרה (Builder pattern)
    public BeamGenerator setUseJitter(boolean useJitter) {
        this.useJitter = useJitter;
        return this;
    }

    /**
     * Generates a grid of 2D points within a square of size [size x size] with Jittering.
     *
     * @param amount The number of points along one side of the grid (e.g., 3 for a 3x3 grid)
     * @param size   The size of the square target area
     * @return A list of Point2D objects
     */
    public List<Point2D> generateGrid(int amount, double size) {
        List<Point2D> points = new ArrayList<>();

        if (amount <= 1) {
            points.add(new Point2D(0, 0));
            return points;
        }

        double step = size / (amount - 1);
        double start = -size / 2;

        for (int i = 0; i < amount; i++) {
            for (int j = 0; j < amount; j++) {
                double x = start + i * step;
                double y = start + j * step;

                // מפעילים אקראיות רק אם הדגל דלוק
                if (useJitter) {
                    x += (RANDOM.nextDouble() - 0.5) * step;
                    y += (RANDOM.nextDouble() - 0.5) * step;
                }

                points.add(new Point2D(x, y));
            }
        }
        return points;
    }

    public Beam generateBeam(Ray centerRay, double size, double distance, int amount) {
        // אם מבקשים קרן אחת, אין טעם לבצע את כל החישובים
        if (amount <= 1) {
            return new Beam(List.of(centerRay));
        }

        Vector vTo = centerRay.direction();

// נבדוק כמה הקרן שלנו מקבילה לציר Z בעזרת מכפלה סקלרית
        Vector zAxis = new Vector(0, 0, 1);
        double dotProd = vTo.dotProduct(zAxis);

// אם הערך המוחלט קרוב ל-1, זה אומר שהוקטור מקביל כמעט לחלוטין לציר Z.
// במצב כזה, שימוש בציר Z למכפלה וקטורית יקריס את התוכנית, לכן נבחר בציר Y.
        Vector tempVector;
        if (Math.abs(dotProd) > 0.9) {
            tempVector = new Vector(0, 1, 0);
        } else {
            tempVector = zAxis;
        }

// עכשיו בטוח לעשות את המכפלות הוקטוריות
        Vector vRight = vTo.crossProduct(tempVector).normalize();
        Vector vUp = vRight.crossProduct(vTo).normalize();

        // 2. צור נקודות דגימה דו-ממדיות
        // תיקון: קריאה נכונה לשיטה באותה מחלקה (בלי new)
        List<Point2D> samples = this.generateGrid(amount, size);
        List<Ray> rays = new ArrayList<>();

        // 3. מיפוי לנקודות תלת-ממדיות ויצירת קרניים
        Point pTarget = centerRay.origin().add(vTo.scale(distance));

        for (Point2D p2d : samples) {
            Point p = pTarget;

            // אופטימיזציה קטנה: נזיז את הנקודה רק אם הערך שונה מאפס
            if (p2d.getX() != 0) {
                p = p.add(vRight.scale(p2d.getX()));
            }
            if (p2d.getY() != 0) {
                p = p.add(vUp.scale(p2d.getY()));
            }

            // בניית הקרן מנקודת המקור אל הנקודה החדשה על המישור
            rays.add(new Ray(centerRay.origin(), p.subtract(centerRay.origin())));
        }

        return new Beam(rays);
    }
}