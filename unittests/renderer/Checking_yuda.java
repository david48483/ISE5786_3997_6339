package renderer;

import geometries.impl.Plane;
import geometries.impl.Sphere;
import lighting.impl.PointLight;
import lighting.impl.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import sampling.impl.JitteredSampler;
import scene.Scene;

public class Checking_yuda {

    @Test
    void testProceduralGridGeneration() {
        // 1. הגדרת הסצנה וצבע הרקע
        Scene scene = new Scene("Procedural Grid Scene")
                .setBackground(new Color(20, 20, 30)); // צבע שמיים כהה/לילה

        // 2. הגדרת חומרים בסיסיים
        Material sphereMaterial = new Material().setKD(0.5).setKS(0.5).setShininess(50);
        Material floorMaterial = new Material().setKD(0.8).setKS(0.2).setShininess(10).setKR(0.9);

        // 3. הוספת רצפה (מישור ענק) כדי לתת פרספקטיבה והצללות
        scene.geometries.add(
                new Plane(new Point(0, -50, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(50, 50, 50))
                        .setMaterial(floorMaterial)
        );

        // 4. יצירה פרוצדורלית של רשת כדורים בעזרת לולאה כפולה
        int rows = 15;
        int cols = 15;
        double spacing = 40.0;
        double radius = 15.0;

        // חישוב אופסט כדי למרכז את הרשת מול המצלמה
        double startX = -(cols * spacing) / 2.0;
        double startZ = 100.0; // הרשת מתחילה בעומק 100 ומתרחקת

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // חישוב הקואורדינטות לכל כדור
                double x = startX + (j * spacing);
                double y = -35.0; // מונחים מעט מעל הרצפה (-50 + 15)
                double z = startZ + (i * spacing);

                // גיוון צבעים מתמטי (לפי המיקום ברשת) כדי שלא יהיה משעמם
                int r = Math.abs((i * 15) % 255);
                int g = Math.abs((j * 15) % 255);
                int b = 150;
                Color dynamicColor = new Color(r, g, b);

                // הוספת הכדור לסצנה
                scene.geometries.add(
                        new Sphere(new Point(x, y, z), radius)
                                .setEmission(dynamicColor)
                                .setMaterial(sphereMaterial)
                );
            }
        }

        // 5. תאורה - שימוש במספר מקורות אור כדי להאיר את כל השדה
        scene.lights.add(
                new SpotLight(new Color(800, 800, 800), new Point(0, 500, -200), new Vector(0, -1, 1))
                        .setKl(1E-5).setKq(1.5E-7)
        );
        scene.lights.add(
                new PointLight(new Color(150, 150, 150), new Point(-200, 100, -100))
                        .setKl(1E-5).setKq(1.5E-7)
        );

        // 6. הגדרת המצלמה (ממוקמת גבוה ומסתכלת באלכסון למטה אל עבר הרשת)
        Camera cameraOn = Camera.getBuilder()
                .setLocation(new Point(0, 300, -300))
                .setDirection(new Vector(0, -0.3, 1), new Vector(0, 1, 0.3)) // וקטורים מאונכים המכוונים פנימה ולמטה
                .setVpDistance(400)
                .setVpSize(200, 200)
                .setResolution(800, 800) // רזולוציה גבוהה לפרטים הקטנים
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setRaysAmount(9)
                .setMultithreading(4)
                .build();

        // 7. ביצוע הרינדור ושמירת התמונה
        System.out.println("Starting procedural render...");
        long startTime = System.currentTimeMillis();

        cameraOn.renderImage();
        cameraOn.writeToImage("check_yuda");

        System.out.println("Render finished in " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds.");
    }
}
