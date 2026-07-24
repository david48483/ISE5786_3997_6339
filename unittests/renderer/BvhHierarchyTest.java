package renderer;

import geometries.impl.Geometries;
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
import sampling.impl.JitteredSampler;
import sampling.impl.TargetShapeType;
import scene.Scene;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit test for rendering a scene with a structured BVH hierarchy containing around 20 geometries,
 * separating infinite planes into a dedicated branch.
 *
 * @author David &amp; Yehuda
 */
public class BvhHierarchyTest {

    /**
     * Default constructor for BvhHierarchyTest.
     */
    public BvhHierarchyTest() {
    }

    /**
     * Tests rendering with a hierarchical geometries structure and BVH acceleration enabled.
     */
    @Test
    public void testBvhHierarchyStructure() {


        // 1. ענף עבור המישורים בלבד (הגופים האינסופיים שיוחזרו כ-null בתיבה התוחמת)
        Geometries planesBranch = new Geometries(
                new Plane(new Point(0, -70, 0), Vector.AXIS_Y)
                        .setMaterial(new Material().setKD(0.2).setKS(0.2).setShininess(30)
                                .setKR(1).setKG(7.0)),
                new Plane(new Point(0, 0, -200), Vector.AXIS_Z)
                        .setMaterial(new Material().setKD(0.5).setKS(0.1).setShininess(10))
        );

        // 2. שלושה ענפים של קבוצות גופים קרובים (יותר מ-50 גופים בסה"כ)
        //    כל ענף מכיל מטריצה צפופה של כדורים עם חומרים בצבעים/שקיפויות/חלביות שונים

        // חומרים לדוגמה: מבריק, שקוף, "חלבי"
        Material glossyMat = new Material().setKD(0.2).setKS(0.8).setShininess(200).setKR(0.6).setKG(4.0);
        Material glassyMat = new Material().setKD(0.2).setKS(0.5).setShininess(120).setKT(0.85);
        Material milkyMat = new Material().setKD(0.4).setKS(0.2).setShininess(40).setKT(0.5).setKB(80.0);

        Geometries clusterA = buildSphereCluster(
                new Point(-80, -10, -90), // מיקום כללי
                4, 4, 1,                   // 16 כדורים
                14, 6,                     // מרווח ורדיוס
                glossyMat,
                new Color(30, 80, 30)
        );

        Geometries clusterB = buildSphereCluster(
                new Point(85, -5, -95),
                4, 4, 1,
                14, 6,
                glassyMat,
                new Color(80, 30, 30)
        );

        Geometries clusterC = buildSphereCluster(
                new Point(0, 35, -110),
                4, 4, 1,
                14, 5.5,
                milkyMat,
                new Color(30, 30, 80)
        );

        // 3. ענף שני של גופים סופיים (למשל, קבוצת משולשים/פירמידות המורכבת מ-12 משולשים שונים)
        Geometries trianglesBranch = new Geometries();
            Point p1 = new Point(-40, -69, -30);
            Point p2 = new Point(-20, -69, -30);
            Point p3 = new Point(-30, -69, -50);
            Point pTop = new Point(-30, -45, -40);
        Material pyramidMat = new Material().setKD(0.5).setKS(0.5).setShininess(60).setKR(0.2).setKG(3.0);
            trianglesBranch.add(
                    new Triangle(p1, p2, pTop).setMaterial(pyramidMat),
                    new Triangle(p2, p3, pTop).setMaterial(pyramidMat),
                    new Triangle(p3, p1, pTop).setMaterial(pyramidMat),
                    new Triangle(p1, p2, p3).setMaterial(pyramidMat)
            );
        // הוספת כמה משולשים "דגל" קטנים ליצירת פרטים נוספים בענף זה
        trianglesBranch.add(
                new Triangle(new Point(15, -69, -60), new Point(25, -69, -60), new Point(20, -60, -50))
                        .setMaterial(new Material().setKD(0.5).setKS(0.2).setShininess(30).setKR(0.1)),
                new Triangle(new Point(35, -69, -60), new Point(45, -69, -60), new Point(40, -58, -48))
                        .setMaterial(new Material().setKD(0.5).setKS(0.2).setShininess(30).setKT(0.3))
        );

        // 4. מבנה העץ הראשי: ענף המישורים + שלוש קבוצות קרובות + ענף המשולשים
        Geometries rootGeometries = new Geometries(planesBranch, clusterA, clusterB, clusterC, trianglesBranch);
        Scene scene = new Scene("BVH Hierarchy Test Scene");
         scene.lights.add(new SpotLight(new Color(700, 400, 400), new Point(60, 50, 100), new Vector(-1, -1, -3))
            .setKl(4E-4).setKq(2E-5));

        scene.lights.add(new PointLight(new Color(500, 250, 250), new Point(-60, -50, 100))
            .setKl(0.0005).setKq(0.0005));

        scene.lights.add(new DirectionalLight(new Color(200, 200, 200), new Vector(1, -1, -1)));


        scene.setGeometries(rootGeometries);


        // בדיקה לדוגמה לוודא שהסצנה הוקמה בהצלחה והגופים נטענו
        assertNotNull(scene, "Scene should not be null");

        Camera.Builder cameraBuilder = Camera.getBuilder()
                .setLocation(new Point(0, 0, 500))
                .setDirection(new Vector(0, 0, -1), Vector.AXIS_Y)
                .setVpSize(200, 200)
                .setVpDistance(500)
                .setDebugPrint(0.3)
                .setSamplerShape(TargetShapeType.CIRCLE)
                .setResolution(600, 600);

        // מריצים תמונות לדוגמה, עם/בלי BVH כדי להמחיש האצה על סצנה עשירה
        scene.setAABB(false);
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(-2)
                .setRaysAmount(9), "BVH-50plus-noBVH-2");
        scene.setAABB(true);
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(-2)
                .setRaysAmount(9), "BVH-50plus-BVH-2");

        scene.setAABB(false);
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(-1)
                .setRaysAmount(9), "BVH-50plus-noBVH-1");

        scene.setAABB(true);
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(-1)
                .setRaysAmount(9), "BVH-50plus-BVH-1");

        scene.setAABB(false);
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(-1)
                .setRaysAmount(9), "BVH-50plus-noBVH-1");

        scene.setBvhTree(true);
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(-1)
                .setRaysAmount(9), "BVH-50plus-BVH-TREE-1");

        scene.setAABB(false);
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(0)
                .setRaysAmount(9), "BVH-50plus-noBVH_0");

        scene.setAABB(true);
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(0)
                .setRaysAmount(9), "BVH-50plus-BVH_0");

        scene.setAABB(false);
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(1)
                .setRaysAmount(9), "BVH-50plus-noBVH_1");

        scene.setAABB(true);
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(1)
                .setRaysAmount(9), "BVH-50plus-BVH_1");

        scene.setAABB(false);
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(4)
                .setRaysAmount(9), "BVH-50plus-noBVH_4");

        scene.setAABB(true);
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(4)
                .setRaysAmount(9), "BVH-50plus-BVH_4");
    }

    /**
     * Helper method to render an image using the configured camera builder and scene.
     *
     * @param scene                   the scene to render
     * @param cameraBuilder           the fully configured camera builder
     * @param glossyDiffusiveFileName the output image file name
     */
    private void createImage(Scene scene, Camera.Builder cameraBuilder, String glossyDiffusiveFileName) {
        long startTime = System.currentTimeMillis();

        Camera cameraOn = cameraBuilder
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

        cameraOn.renderImage();
        cameraOn.writeToImage(glossyDiffusiveFileName);

        long endTime = System.currentTimeMillis();
        System.out.println("Render time " + glossyDiffusiveFileName + ": " + (endTime - startTime) / 1000.0 + " seconds.\n");
    }

    /**
     * יוצר קבוצה צפופה של כדורים סביב נקודת מוצא במטריצה בגודל nx*ny*nz.
     *
     * @param origin   מרכז כללי לקבוצה
     * @param nx       כמות כדורים בציר X
     * @param ny       כמות כדורים בציר Y
     * @param nz       כמות כדורים בציר Z
     * @param spacing  מרחק בין מרכזי הכדורים
     * @param radius   רדיוס בסיס לכל כדור
     * @param mat      חומר לשיוך לכדורים
     * @param emission צבע פליטה לכדורים
     * @return ענף גיאומטריות המכיל את הכדורים
     */
    private Geometries buildSphereCluster(Point origin, int nx, int ny, int nz,
                                          double spacing, double radius,
                                          Material mat, Color emission) {
        Geometries g = new Geometries();
        double startX = origin.getX() - (nx - 1) * spacing * 0.5;
        double startY = origin.getY() - (ny - 1) * spacing * 0.5;
        double startZ = origin.getZ() - (nz - 1) * spacing * 0.5;

        for (int ix = 0; ix < nx; ix++) {
            for (int iy = 0; iy < ny; iy++) {
                for (int iz = 0; iz < nz; iz++) {
                    Point c = new Point(
                            startX + ix * spacing,
                            startY + iy * spacing,
                            startZ + iz * spacing
                    );
                    // וריאציה קלה ברדיוס כדי לשבור סימטריה
                    double r = radius * (0.85 + 0.3 * ((ix + iy + iz) % 3) / 3.0);
                    g.add(new Sphere(c, r)
                            .setEmission(emission)
                            .setMaterial(mat));
                }
            }
        }
        return g;
    }
}