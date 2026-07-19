package renderer;

import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.impl.DirectionalLight;
import lighting.impl.PointLight;
import lighting.impl.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import scene.Scene;

public class GlossyDiffusiveTests {

    @Test
    public void testGlossyAndDiffusiveSimulation() {
        Scene scene = new Scene("Glossy and Diffusive Simulation Scene");

        // ====================================================================
        // 1. הגדרת 3 מקורות אור (חלק 5 - לפחות 3 מקורות אור)
        // ====================================================================
        scene.lights.add(new SpotLight(new Color(700, 400, 400), new Point(60, 50, 100), new Vector(-1, -1, -3))
                .setKl(4E-4).setKq(2E-5));

        scene.lights.add(new PointLight(new Color(500, 250, 250), new Point(-60, -50, 100))
                .setKl(0.0005).setKq(0.0005));

        scene.lights.add(new DirectionalLight(new Color(200, 200, 200), new Vector(1, -1, -1)));

        // ====================================================================
        // 2. הגדרת 10 גופים במרחב (חלק 5 - לפחות 10 גופים)
        // ====================================================================

        // גוף 1: רצפת מראה מטושטשת (Glossy Surface עם kG)
        scene.geometries.add(new Plane(new Point(0, -70, 0), new Vector(0, 1, 0))
                .setMaterial(new Material().setKD(0.2).setKS(0.2).setShininess(30)
                        .setKR(0.7).setKG(3.0))); // kG = רדיוס טשטוש השתקפות

        // גוף 2: קיר רקע מט
        scene.geometries.add(new Plane(new Point(0, 0, -200), new Vector(0, 0, 1))
                .setMaterial(new Material().setKD(0.5).setKS(0.1).setShininess(10)));

        // גוף 3: כדור מרכזי עשוי זכוכית חלבית (Diffusive Glass עם kB)
        scene.geometries.add(new Sphere(new Point(0, 0, -50), 30)
                .setEmission(new Color(20, 40, 80))
                .setMaterial(new Material().setKD(0.2).setKS(0.3).setShininess(100)
                        .setKT(0.8).setKB(4.0))); // kB = רדיוס טשטוש שבירה

        // גוף 4: כדור ימני - מראה מבריקה חלקה (לצורך השוואה קונטרסטית)
        scene.geometries.add(new Sphere(new Point(70, 20, -70), 20)
                .setEmission(new Color(30, 0, 0))
                .setMaterial(new Material().setKD(0.2).setKS(0.8).setShininess(200)
                        .setKR(0.9).setKG(0.0))); // חלק לחלוטין

        // גוף 5: כדור שמאלי - פלסטיק ירוק מט
        scene.geometries.add(new Sphere(new Point(-70, 20, -70), 20)
                .setEmission(new Color(0, 40, 0))
                .setMaterial(new Material().setKD(0.7).setKS(0.2).setShininess(50)));

        // גוף 6: כדור קטן אחורי
        scene.geometries.add(new Sphere(new Point(0, 60, -120), 10)
                .setEmission(new Color(80, 80, 0))
                .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(30)));

        // גופים 7, 8, 9, 10: ארבעה משולשים היוצרים פירמידה קטנה בצד הסצנה
        Point p1 = new Point(-40, -69, -30);
        Point p2 = new Point(-20, -69, -30);
        Point p3 = new Point(-30, -69, -50);
        Point pTop = new Point(-30, -45, -40);

        Material pyramidMat = new Material().setKD(0.5).setKS(0.5).setShininess(60);
        scene.geometries.add(new Triangle(p1, p2, pTop).setMaterial(pyramidMat)); // גוף 7
        scene.geometries.add(new Triangle(p2, p3, pTop).setMaterial(pyramidMat)); // גוף 8
        scene.geometries.add(new Triangle(p3, p1, pTop).setMaterial(pyramidMat)); // גוף 9
        scene.geometries.add(new Triangle(p1, p2, p3).setMaterial(pyramidMat));    // גוף 10

        // ====================================================================
        // 3. הגדרת המצלמה
        // ====================================================================
        Camera.Builder cameraBuilder = Camera.getBuilder()
                .setLocation(new Point(0, 0, 500))
                .setDirection(new Vector(0, 0, -1), Vector.AXIS_Y)
                .setVpSize(200, 200)
                .setVpDistance(500)
                .setResolution(600, 600);

        // --------------------------------------------------------------------
        // הרצה ראשונה: ללא השיפור (חישוב מהיר, קרן יחידה)
        // --------------------------------------------------------------------
        long startTime = System.currentTimeMillis();

        SimpleRayTracer tracerOff = new SimpleRayTracer(scene)
                .setUseAdvancedEffects(false); // כבוי!

        Camera cameraOff = cameraBuilder
                .setRayTracer(tracerOff)
                .build();

        cameraOff.renderImage();
        cameraOff.writeToImage("GlossyDiffusive_DISABLED");

        long endTime = System.currentTimeMillis();
        System.out.println("Render time WITHOUT advanced effects: " + (endTime - startTime) / 1000.0 + " seconds.");

        // --------------------------------------------------------------------
        // הרצה שנייה: עם השיפור (ריבוי דגימות באלומה - אלומה של 9x9 = 81 קרניים)
        // --------------------------------------------------------------------
        startTime = System.currentTimeMillis();

        SimpleRayTracer tracerOn = new SimpleRayTracer(scene)
                .setUseAdvancedEffects(true)  // מופעל!
                .setRaysAmount(9);            // רשת של 9x9 קרניים לכל השתקפות/שבירה מחוספסת

        Camera cameraOn = cameraBuilder
                .setRayTracer(tracerOn)
                // אם מימשתם מנגנון Multi-threading במצלמה, זה הזמן להפעיל אותו כאן!
                .setMultithreading(4)

                .build();

        cameraOn.renderImage();
        cameraOn.writeToImage("GlossyDiffusive_ENABLED");

        endTime = System.currentTimeMillis();
        System.out.println("Render time WITH advanced effects: " + (endTime - startTime) / 1000.0 + " seconds.");
    }
}