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

        scene.lights.add(new SpotLight(new Color(700, 400, 400), new Point(60, 50, 100), new Vector(-1, -1, -3))
                .setKl(4E-4).setKq(2E-5));

        scene.lights.add(new PointLight(new Color(500, 250, 250), new Point(-60, -50, 100))
                .setKl(0.0005).setKq(0.0005));

        scene.lights.add(new DirectionalLight(new Color(200, 200, 200), new Vector(1, -1, -1)));

        // גוף 1: רצפת מראה מטושטשת (Glossy Surface עם kG)
        scene.geometries.add(new Plane(new Point(0, -70, 0), Vector.AXIS_Y)
                .setMaterial(new Material().setKD(0.2).setKS(0.2).setShininess(30)
                        .setKR(1).setKG(7.0))); // kG = רדיוס טשטוש השתקפות

        // גוף 2: קיר רקע מט
        scene.geometries.add(new Plane(new Point(0, 0, -200), Vector.AXIS_Z)
                .setMaterial(new Material().setKD(0.5).setKS(0.1).setShininess(10)));

        // גוף 3: כדור מרכזי עשוי זכוכית חלבית (Diffusive Glass עם kB)
        scene.geometries.add(new Sphere(new Point(0, 0, -50), 30)
                .setEmission(new Color(20, 40, 80))
                .setMaterial(new Material().setKD(0.2).setKS(0.5).setShininess(70)
                        .setKT(0.8).setKB(100.0))); // kB = רדיוס טשטוש שבירה

        // גוף 4: כדור ימני - מראה מבריקה חלקה (לצורך השוואה קונטרסטית)
        scene.geometries.add(new Sphere(new Point(70, 20, -70), 20)
                .setEmission(new Color(30, 0, 0))
                .setMaterial(new Material().setKD(0.2).setKS(0.8).setShininess(200)
                        .setKR(1)));

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
        scene.geometries.add(new Triangle(p1, p2, pTop).setMaterial(pyramidMat));
        scene.geometries.add(new Triangle(p2, p3, pTop).setMaterial(pyramidMat));
        scene.geometries.add(new Triangle(p3, p1, pTop).setMaterial(pyramidMat));
        scene.geometries.add(new Triangle(p1, p2, p3).setMaterial(pyramidMat));

        Camera.Builder cameraBuilder = Camera.getBuilder()
                .setLocation(new Point(0, 0, 500))
                .setDirection(new Vector(0, 0, -1), Vector.AXIS_Y)
                .setVpSize(200, 200)
                .setVpDistance(500)
                .setResolution(600, 600);

        long startTime = System.currentTimeMillis();

        SimpleRayTracer tracerOff = new SimpleRayTracer(scene)
                .setUseAdvancedEffects(false);

        Camera cameraOff = cameraBuilder
                .setRayTracer(tracerOff)
                .setMultithreading(4)
                .build();

        cameraOff.renderImage();
        cameraOff.writeToImage("GlossyDiffusive_DISABLED");

        long endTime = System.currentTimeMillis();
        System.out.println("Render time WITHOUT advanced effects: " + (endTime - startTime) / 1000.0 + " seconds.");

        startTime = System.currentTimeMillis();

        SimpleRayTracer tracerOn = new SimpleRayTracer(scene)
                .setUseAdvancedEffects(true)
                .setRaysAmount(9);

        Camera cameraOn = cameraBuilder
                .setRayTracer(tracerOn)

                .setMultithreading(4)

                .build();

        cameraOn.renderImage();
        cameraOn.writeToImage("GlossyDiffusive_ENABLED");

        endTime = System.currentTimeMillis();
        System.out.println("Render time WITH advanced effects: " + (endTime - startTime) / 1000.0 + " seconds.");
    }

    @Test
    public void testSef() {
        Scene scene = new Scene("testYehuda").setBackground(new Color(10, 10, 10));

        scene.lights.add(new DirectionalLight(new Color(100, 100, 100), new Vector(0, -1, -1)));

        scene.lights.add(new PointLight(new Color(250, 250, 250), new Point(0, 80, 50))
                .setKl(0.001).setKq(0.0001));

        scene.lights.add(new SpotLight(new Color(255, 200, 100), new Point(0, 50, 80), new Vector(0, -1, -2))
                .setKl(0.0005).setKq(0.00005));

        // רצפה אטומה
        scene.geometries.add(new Plane(new Point(0, -50, 0), Vector.AXIS_Y)
                .setEmission(new Color(50, 50, 50))
                .setMaterial(new Material().setKD(0.7).setKS(0.1).setShininess(10)));

        // קיר אחורי אטום
        scene.geometries.add(new Plane(new Point(0, 0, -200), Vector.AXIS_Z)
                .setEmission(new Color(20, 50, 100))
                .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(20)));

        // כדור אדום מבריק במרכז
        scene.geometries.add(new Sphere(new Point(0, -20, -120), 30)
                .setEmission(new Color(200, 0, 0))
                .setMaterial(new Material().setKD(0.3).setKS(1.0).setShininess(300)));

        // --- מראה מושלמת (צד ימין) ---

        Point mP1 = new Point(30, -10, -198);
        Point mP2 = new Point(80, -10, -198);
        Point mP3 = new Point(80, 40, -198);
        Point mP4 = new Point(30, 40, -198);
        Material mirrorMat = new Material().setKR(1.0);

        scene.geometries.add(new Triangle(mP1, mP2, mP3).setMaterial(mirrorMat));
        scene.geometries.add(new Triangle(mP1, mP3, mP4).setMaterial(mirrorMat));

        // --- מתכת מוברשת / מראה מטושטשת (צד שמאל) ---

        Point bmP1 = new Point(-130, -20, -198);
        Point bmP2 = new Point(-40, -20, -198);
        Point bmP3 = new Point(-85, 80, -198);
        Material brushedMetalMat = new Material().setKR(0.8).setKG(15.0);

        scene.geometries.add(new Triangle(bmP1, bmP2, bmP3)
                .setEmission(new Color(20, 20, 20))
                .setMaterial(brushedMetalMat));

        // --- חלון זכוכית חלבית (קדמי, מסתיר חצי מהכדור) ---

        Point gP1 = new Point(-40, -20, -70);
        Point gP2 = new Point(0, -20, -70);
        Point gP3 = new Point(0, 30, -70);
        Point gP4 = new Point(-40, 30, -70);
        Material frostedGlassMat = new Material().setKT(0.95).setKB(5.0);

        scene.geometries.add(new Triangle(gP1, gP2, gP3)
                .setEmission(new Color(10, 20, 30))
                .setMaterial(frostedGlassMat));
        scene.geometries.add(new Triangle(gP1, gP3, gP4)
                .setEmission(new Color(10, 20, 30))
                .setMaterial(frostedGlassMat));

        Camera.Builder cameraBuilder = Camera.getBuilder()
                .setLocation(new Point(0, 10, 150))
                .setDirection(new Vector(0, 0, -1), Vector.AXIS_Y)
                .setVpSize(200, 200)
                .setVpDistance(150)
                .setResolution(600, 600);

        long startTime = System.currentTimeMillis();

        SimpleRayTracer tracerOff = new SimpleRayTracer(scene)
                .setUseAdvancedEffects(false);

        Camera cameraOff = cameraBuilder
                .setRayTracer(tracerOff)
                .setMultithreading(4)
                .build();

        cameraOff.renderImage();
        cameraOff.writeToImage("SelfGlossyDiffusive_DISABLED");

        long endTime = System.currentTimeMillis();
        System.out.println("Render time WITHOUT advanced effects: " + (endTime - startTime) / 1000.0 + " seconds.");

        startTime = System.currentTimeMillis();

        SimpleRayTracer tracerOn = new SimpleRayTracer(scene)
                .setUseAdvancedEffects(true)
                .setRaysAmount(9)
                .setTargetDistance(100d);

        Camera cameraOn = cameraBuilder
                .setRayTracer(tracerOn)
                .setMultithreading(4)
                .build();

        cameraOn.renderImage();
        cameraOn.writeToImage("SelfGlossyDiffusive_ENABLED");

        endTime = System.currentTimeMillis();
        System.out.println("Render time WITH advanced effects: " + (endTime - startTime) / 1000.0 + " seconds.");
    }
}