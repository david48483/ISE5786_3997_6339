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
     * Generates a circular grid of 2D points with Jittering.
     *
     * @param amount The base number of points along one side (before circular compensation)
     * @param size   The diameter of the circular target area
     * @return A list of Point2D objects inside the circle
     */
    public List<Point2D> generateGrid(int amount, double size) {
        List<Point2D> points = new ArrayList<>();

        if (amount <= 1) {
            points.add(new Point2D(0, 0));
            return points;
        }

        // --- התיקון לעיגול ---
        // כדי שבסוף יישארו לנו בערך amount * amount נקודות בתוך העיגול,
        // אנחנו מגדילים את גודל הרשת ההתחלתי. השורש של 4 חלקי פאי הוא בערך 1.128.
        int effectiveAmount = (int) Math.ceil(amount * Math.sqrt(4 / Math.PI));

        double step = size / (effectiveAmount - 1);
        double start = -size / 2;
        double radius = size / 2;
        double radiusSq = radius * radius; // שומרים את הרדיוס בריבוע כדי לחסוך פעולת שורש בבדיקה

        for (int i = 0; i < effectiveAmount; i++) {
            for (int j = 0; j < effectiveAmount; j++) {
                double x = start + i * step;
                double y = start + j * step;

                // מפעילים אקראיות (Jittering) אם הדגל דלוק
                if (useJitter) {
                    x += (RANDOM.nextDouble() - 0.5) * step;
                    y += (RANDOM.nextDouble() - 0.5) * step;
                }

                // --- סינון הנקודות שמעבר לרדיוס (משוואת המעגל) ---
                // בודקים האם x^2 + y^2 קטן או שווה ל-R^2
                if (x * x + y * y <= radiusSq) {
                    points.add(new Point2D(x, y));
                }
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

        double dotProd = vTo.dotProduct(Vector.AXIS_Z);

// אם הערך המוחלט קרוב ל-1, זה אומר שהוקטור מקביל כמעט לחלוטין לציר Z.
// במצב כזה, שימוש בציר Z למכפלה וקטורית יקריס את התוכנית, לכן נבחר בציר Y.
        Vector tempVector;
        if (Math.abs(dotProd) > 0.9) {
            tempVector = Vector.AXIS_Y;
        } else {
            tempVector = Vector.AXIS_Z;
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

            // אופטימיזציה נזיז את הנקודה רק אם הערך שונה מאפס
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