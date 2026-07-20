package renderer;

import geometries.impl.Plane;
import geometries.impl.Polygon;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.impl.AmbientLight;
import lighting.impl.PointLight;
import lighting.impl.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import scene.Scene;

import static java.awt.Color.*;

/**
 * Collection of renderer-focused image tests that exercise reflection, refraction, and shadow behavior.
 */
public class CustomImageTests {

    /**
     * Creates a new custom image test suite.
     */
    public CustomImageTests() {
    }

    /**
     * Test for Stage 8 requirement: 3-4 objects demonstrating reflection, refraction, and shadows.
     */
    @Test
    public void stage8CustomImageTest() {
        Scene scene = new Scene("Stage 8 Custom Scene")
                .setAmbientLight(new AmbientLight(new Color(40, 40, 40)));

        Material mirrorMat = new Material().setKD(0.2).setKS(0.5).setShininess(40).setKR(0.5);
        Material glassMat = new Material().setKD(0.1).setKS(0.2).setShininess(30).setKT(0.8);
        Material matteMat = new Material().setKD(0.8).setKS(0.2).setShininess(20);

        scene.geometries.add(
                // 1. רצפת מראה (השתקפות)
                new Plane(new Point(0, 0, -20), new Vector(0, 0, 1))
                        .setEmission(new Color(30, 30, 30))
                        .setMaterial(mirrorMat),

                // 2. כדור זכוכית (שקיפות - מטיל צל חלקי)
                new Sphere(new Point(0, 0, 10), 20)
                        .setEmission(new Color(BLACK))
                        .setMaterial(glassMat),

                // 3. כדור אטום קטן בתוך/מאחורי כדור הזכוכית
                new Sphere(new Point(5, 5, 5), 8)
                        .setEmission(new Color(RED))
                        .setMaterial(matteMat),

                // 4. משולש מרחף שמטיל צל
                new Triangle(new Point(-30, 30, 40), new Point(-10, 30, 40), new Point(-20, 50, 60))
                        .setEmission(new Color(BLUE))
                        .setMaterial(matteMat)
        );

        scene.lights.add(new SpotLight(new Color(700, 400, 400), new Point(40, 40, 100), new Vector(-1, -1, -1))
                .setKl(0.0001).setKq(0.00005));

        Camera camera = Camera.getBuilder()
                .setLocation(new Point(0, -100, 30))
                .setDirection(new Vector(0, 1, -0.2), new Vector(0, 0.2, 1))
                .setVpSize(200, 200)
                .setVpDistance(100)
                // תוקן: שימוש בפונקציות הנכונות של הבילדר שלך
                .setResolution(500, 500)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

        camera.renderImage();
        camera.writeToImage("stage8_custom_image");
    }

    /**
     * Test for Bonus 1: 10+ objects, all geometries, demonstrating all features.
     */
    @Test
    public void bonus1ComplexImageTest() {
        Scene scene = new Scene("Bonus 1 Scene")
                .setAmbientLight(new AmbientLight(new Color(25, 25, 25)));

        Material mirror = new Material().setKD(0.0).setKS(0.8).setShininess(60).setKR(0.7);
        Material weakMirror = new Material().setKD(0.0).setKS(0.8).setShininess(60).setKR(0.3);
        Material ceilingMirror = new Material().setKD(0.0).setKS(0.8).setShininess(60).setKR(0.1);

        Material semiTransparent = new Material().setKD(0.2).setKS(0.2).setShininess(30).setKT(0.6);
        Material shinyMat = new Material().setKD(0.5).setKS(0.5).setShininess(100);

        scene.geometries.add(
                // 1. רצפה רפלקטיבית
                new Plane(new Point(0, 0, -50), new Vector(0, 0, 1))
                        .setEmission(new Color(15, 15, 25))
                        .setMaterial(mirror),

                // 2. קיר אחורי רפלקטיבי (מצולע)
                new Polygon(new Point(-200, 150, -50), new Point(200, 150, -50),
                        new Point(200, 150, 200), new Point(-200, 150, 200))
                        .setEmission(new Color(10, 0, 10))
                        .setMaterial(weakMirror),

                // 3-6. 4 כדורים מסודרים בשורה (חלקם שקופים, חלקם אטומים)
                new Sphere(new Point(-45, 0, 0), 15).setEmission(new Color(CYAN)).setMaterial(shinyMat),
                new Sphere(new Point(-15, 0, 0), 15).setEmission(new Color(BLACK)).setMaterial(semiTransparent),
                new Sphere(new Point(15, 0, 0), 15).setEmission(new Color(MAGENTA)).setMaterial(shinyMat),
                new Sphere(new Point(45, 0, 0), 15).setEmission(new Color(BLACK)).setMaterial(semiTransparent),

                // 7-10. פירמידה (4 משולשים) שמרחפת מעל
                new Triangle(new Point(0, 20, 60), new Point(20, 0, 40), new Point(-20, 0, 40))
                        .setEmission(new Color(YELLOW)).setMaterial(shinyMat),
                new Triangle(new Point(0, 20, 60), new Point(20, 0, 40), new Point(0, 40, 40))
                        .setEmission(new Color(ORANGE)).setMaterial(shinyMat),
                new Triangle(new Point(0, 20, 60), new Point(-20, 0, 40), new Point(0, 40, 40))
                        .setEmission(new Color(RED)).setMaterial(shinyMat),
                new Triangle(new Point(20, 0, 40), new Point(-20, 0, 40), new Point(0, 40, 40))
                        .setEmission(new Color(DARK_GRAY)).setMaterial(shinyMat),

                // 11. מישור נוסף כ"תקרה" כדי לייצר עוד השתקפויות
                new Plane(new Point(0, 0, 150), new Vector(0, 0, -1))
                        .setEmission(new Color(5, 5, 5)).setMaterial(ceilingMirror)
        );

        scene.lights.add(new SpotLight(new Color(500, 500, 500), new Point(0, -100, 100), new Vector(0, 1, -0.5))
                .setKl(0.0001).setKq(0.00001));
        scene.lights.add(new PointLight(new Color(0, 255, 0), new Point(0, 10, 20))
                .setKl(0.001).setKq(0.0002));

        Camera camera = Camera.getBuilder()
                .setLocation(new Point(0, -150, 30))
                .setDirection(new Vector(0, 1, -0.1), new Vector(0, 0.1, 1))
                .setVpSize(200, 200)
                .setVpDistance(100)
                // תוקן: שימוש בפונקציות הנכונות של הבילדר שלך
                .setResolution(800, 800)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

        camera.renderImage();
        camera.writeToImage("bonus1_complex_image");
    }
}