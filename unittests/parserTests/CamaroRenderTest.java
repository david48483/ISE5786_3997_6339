package parserTests;

import geometries.impl.Plane;
import geometries.impl.Sphere;
import lighting.impl.PointLight;
import org.junit.jupiter.api.Test;
import parser.ObjParser;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import renderer.Camera;
import renderer.RayTracerType;
import scene.Scene;

/**
 * Integration test that parses an OBJ file (Camaro model), adds it to a scene,
 * and renders the result with basic lighting and background geometry.
 */
public class CamaroRenderTest {
    /**
     * Default constructor for the test class.
     */
    public CamaroRenderTest() {
    }
    /**
     * Creates a matrix-based neon LED sign and attaches it to the provided scene.
     *
     * @param scene the scene to which the neon sign geometries and lights will be added
     */
    private void createNeonSign(Scene scene) {
        // Matrix drawing the text (left to right)
        int[][] textMatrix = {
                {1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1},
                {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1}
        };

        Material ledMat = new Material().setKD(0).setKS(0); // does not receive light, only emits
        Color ledColor = new Color(255, 180, 20); // warm neon orange

        // Adjusted dimensions for a long sentence
        double radius = 0.08;
        double spacing = 0.18;

        // Starting position for the sign: left (to center it), up, and slightly in front of the back wall
        double startX = -10;
        double startY = 3.5;
        double startZ = -5.8;

        // Create spheres
        for (int row = 0; row < textMatrix.length; row++) {
            for (int col = 0; col < textMatrix[row].length; col++) {
                if (textMatrix[row][col] == 1) {
                    double x = startX + col * spacing;
                    double y = startY - row * spacing;

                    Sphere led = new Sphere(new Point(x, y, startZ), radius);
                    led.setEmission(ledColor);
                    led.setMaterial(ledMat);
                    scene.geometries.add(led);
                }
            }
        }

        // 3 ghost lights in the air to evenly distribute illumination across the wide sign
        scene.lights.add(new PointLight(new Color(255, 180, 20), new Point(-4, 2.5, -4.5))
                .setKl(0.05).setKq(0.005));
        scene.lights.add(new PointLight(new Color(255, 180, 20), new Point(0, 2.5, -4.5))
                .setKl(0.05).setKq(0.005));
        scene.lights.add(new PointLight(new Color(255, 180, 20), new Point(4, 2.5, -4.5))
                .setKl(0.05).setKq(0.005));
    }

    /**
     * Parses the Camaro OBJ and renders the scene to an image to validate end-to-end integration.
     */
    @Test
    void testCamaroRender() {
        // 1. Set up the scene and a dark background so the car stands out
        Scene scene = new Scene("Camaro Test Scene")
                .setBackground(new Color(15, 15, 20));

        // 2. Set the car body material (shiny so lighting reflections are visible)
        Material carMaterial = new Material().setKD(0.5).setKS(0.5).setShininess(300).setKR(1);
        Color carColor = new Color(200, 20, 20); // sporty red car

        String desktop = "C:\\Users\\admin\\Desktop\\Camaro.obj";
        // 3. Call the parser! (make sure the camaro.obj file is in the project base directory)
        System.out.println("Loading OBJ file... This might take a few seconds.");
        ObjParser.parseAndAdd(desktop, scene, carMaterial, carColor);
        System.out.println("Loaded " + " triangles successfully.");
        System.out.println("Finished loading geometry!");

        // 4. Add lighting (replaces the studio lights removed in Blender)
        // Strong light from above and to the right
        scene.lights.add(new PointLight(new Color(800, 800, 800), new Point(200, 300, 100))
                .setKl(0.0001).setKq(0.00005));

        // Weaker "fill" light from the left to soften shadows
        scene.lights.add(new PointLight(new Color(300, 300, 400), new Point(-200, 100, 150))
                .setKl(0.0001).setKq(0.00005));

        scene.geometries.add(
                new Plane(new Point(0, -1, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(50, 50, 50))
                        .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(10).setKR(0.2).setKG(7.0).setKB(100.0))
        );
        // קיר אחורי שפונה לעבר המצלמה
        scene.geometries.add(new Plane(new Point(-10, 0, -10), new Vector(1, 0, 1))
                .setEmission(new Color(20, 20, 20)) // צבע אפור כהה
                .setMaterial(new Material().setKD(0.5).setKS(0.1)));

       /* Material ledMat = new Material().setKD(0).setKS(0); // לד לא מגיב לאור חיצוני, הוא רק פולט
        Color ledColor = new Color(255, 200, 50); // אור צהוב-כתום חם
        double ledRadius = 0.15; // גודל כל נורה*/
// 1. הוספת קיר אחורי אפור וקצת מבריק שיושב מאחורי הרכב
        scene.geometries.add(new Plane(new Point(0, 0, -6), new Vector(0, 0, 1))
                .setEmission(new Color(20, 20, 20))
                .setMaterial(new Material().setKD(0.5).setKS(0.1)));

// 2. הפעלת הפונקציה ששמה את השלט והתאורה על הקיר
        createNeonSign(scene);
        // 5. הגדרת המצלמה
        Camera camera = Camera.getBuilder()
                // מיקום המצלמה: מתרחקים קצת לאחור ומעלמים מעט למעלה
                .setLocation(new Point(3, 2, 3))
                // מסתכלים לכיוון המרכז (0,0,0) איפה שהרכב נמצא
                .setDirection(new Vector(-3, -2, -3), new Vector(-1, 3, -1)) // וקטורים מאונכים המכוונים פנימה ולמטה
                .setVpSize(200, 200)
                .setVpDistance(100)
                .setResolution(50, 50) // רזולוציה גבוהה
                // אם מימשת את שיטת ה-Jittering למניעת אליאסינג (Anti-Aliasing), זה הזמן להשתמש בה!
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setMultithreading(8)
                .setDebugPrint(0.1)
                .build();

        // 6. צילום ויצירת הקובץ
        System.out.println("Starting render engine...");
        long startTime = System.currentTimeMillis();

        camera.renderImage();
        camera.writeToImage("camaro_final_render");

        System.out.println("Render complete in " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds!");
    }
}