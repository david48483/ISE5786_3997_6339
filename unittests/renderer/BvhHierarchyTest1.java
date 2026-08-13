package renderer;

import geometries.api.Intersectable;
import geometries.impl.Geometries;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.impl.DirectionalLight;
import lighting.impl.PointLight;
import lighting.impl.SpotLight;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import sampling.impl.JitteredSampler;
import sampling.impl.TargetShapeType;
import scene.Scene;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit test for rendering a scene with a structured BVH hierarchy,
 * comparing Flat, Manual, and Automatic BVH configurations.
 *
 * @author David & Yehuda
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class BvhHierarchyTest1 {

    private static Scene scene;
    private static Camera.Builder cameraBuilder;

    private static Geometries flatScene;
    private static Geometries manualHierarchy;
    private static Geometries autoHierarchy;

    private static final int MT_THREADS = -1; // optimal thread count for multithreading

    /**
     * Static helper method that builds the scene once for the entire measurement series.
     */
    @BeforeAll
    public static void setupScene() {

        // Matte material - absorbs a lot of light, barely shiny
        Material matteMat = new Material().setKD(0.8).setKS(0.2).setShininess(20);

// Shiny plastic material - produces a nice specular highlight
        Material shinyMat = new Material().setKD(0.5).setKS(0.5).setShininess(100);

// Opaque metallic material - strong and very focused highlight, no ambient reflection
        Material metallicMat = new Material().setKD(0.3).setKS(0.8).setShininess(300);
        // 1. הכנת החומרים
        Material glossyMat = new Material().setKD(0.2).setKS(0.8).setShininess(200).setKR(0.6).setKG(4.0);
        Material glassyMat = new Material().setKD(0.2).setKS(0.5).setShininess(120).setKT(0.85);
        Material milkyMat = new Material().setKD(0.4).setKS(0.2).setShininess(40).setKT(0.5).setKB(80.0);
        Material pyramidMat = new Material().setKD(0.5).setKS(0.5).setShininess(60).setKR(0.2).setKG(3.0);

        // 2. Build manual hierarchy branches (state 2) - hundreds of bodies to demonstrate acceleration
        Geometries planesBranch = new Geometries(
                new Plane(new Point(0, -70, 0), Vector.AXIS_Y)
                        .setMaterial(new Material().setKD(0.2).setKS(0.2).setShininess(30).setKR(1).setKG(7.0)),
                new Plane(new Point(0, 0, -200), Vector.AXIS_Z)
                        .setMaterial(new Material().setKD(0.5).setKS(0.1).setShininess(10))
        );

        // Small and denser clusters (e.g. 8x8x8 = 512 spheres per cluster - over 1500 spheres total)
        Geometries clusterA = buildSphereCluster(new Point(-60, -10, -150), 8, 8, 8, 12, 5, matteMat, new Color(30, 80, 30));
        Geometries clusterB = buildSphereCluster(new Point(60, -5, -150), 8, 8, 8, 12, 5, shinyMat, new Color(80, 30, 30));
        Geometries clusterC = buildSphereCluster(new Point(0, 35, -180), 8, 8, 8, 12, 4.5, metallicMat, new Color(30, 30, 80));
        Geometries trianglesBranch = new Geometries();
        Point p1 = new Point(-40, -69, -30), p2 = new Point(-20, -69, -30), p3 = new Point(-30, -69, -50), pTop = new Point(-30, -45, -40);
        trianglesBranch.add(
                new Triangle(p1, p2, pTop).setMaterial(pyramidMat),
                new Triangle(p2, p3, pTop).setMaterial(pyramidMat),
                new Triangle(p3, p1, pTop).setMaterial(pyramidMat),
                new Triangle(p1, p2, p3).setMaterial(pyramidMat)
        );

        // 3. Create three structural states in memory (only via the public API of Geometries!)

        // State 1: Manual hierarchy
        manualHierarchy = new Geometries( clusterA, clusterB, clusterC, trianglesBranch);

        // State 2: Fully flattened scene
        flatScene = manualHierarchy.flatten();

        // State 3: Automatic BVH hierarchy
        // Create another flat copy, then tell it to build itself as a tree.
        autoHierarchy = manualHierarchy.flatten();

        long startTreeTime = System.currentTimeMillis();
        // Fully legal call that preserves encapsulation!
        // This method triggers the internal BvhBuilder and reorganizes autoHierarchy as a tree.
        autoHierarchy.buildBvhTree();
        long endTreeTime = System.currentTimeMillis();

        System.out.println("--- Overhead: Auto BVH Tree built in " + (endTreeTime - startTreeTime) / 1000.0 + " seconds ---");
        // 4. Set up the general scene and camera
        scene = new Scene("BVH Hierarchy Test Scene");
        scene.lights.add(new SpotLight(new Color(700, 400, 400), new Point(60, 50, 100), new Vector(-1, -1, -3)).setKl(4E-4).setKq(2E-5));
        scene.lights.add(new PointLight(new Color(500, 250, 250), new Point(-60, -50, 100)).setKl(0.0005).setKq(0.0005));
        scene.lights.add(new DirectionalLight(new Color(200, 200, 200), new Vector(1, -1, -1)));
        assertNotNull(scene, "Scene should not be null");

        cameraBuilder = Camera.getBuilder()
                .setLocation(new Point(0, 0, 500))
                .setDirection(new Vector(0, 0, -1), Vector.AXIS_Y)
                .setVpSize(200, 200)
                .setVpDistance(500)
                .setDebugPrint(0.5)
                .setSamplerShape(TargetShapeType.CIRCLE)
                .setResolution(800, 800)
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setRaysAmount(9);
    }

    // =========================================================
    // Flat scene measurements
    // =========================================================

    @Test
    public void test01_Flat_NoCBR_NoMT() {
        runMeasurement(flatScene, false, false, 0, "01-Flat-NoCBR-NoMT.");
    }

    @Test
    public void test02_Flat_WithCBR_NoMT() {
        runMeasurement(flatScene, true, false, 0, "02-Flat-WithCBR-NoMT.");
    }

    @Test
    public void test03_Flat_NoCBR_MT() {
        runMeasurement(flatScene, false, false, MT_THREADS, "03-Flat-NoCBR-MT.");
    }

    @Test
    public void test04_Flat_WithCBR_MT() {
        runMeasurement(flatScene, true, false, MT_THREADS, "04-Flat-WithCBR-MT.");
    }

    // =========================================================
    // Manual BVH hierarchy measurements
    // =========================================================

    @Test
    public void test05_Manual_NoCBR_NoMT() {
        runMeasurement(manualHierarchy, false, false, 0, "05-Manual-NoCBR-NoMT.");
    }

    @Test
    public void test06_Manual_WithCBR_NoMT() {
        runMeasurement(manualHierarchy, true, false, 0, "06-Manual-WithCBR-NoMT.");
    }

    @Test
    public void test07_Manual_NoCBR_MT() {
        runMeasurement(manualHierarchy, false, false, MT_THREADS, "07-Manual-NoCBR-MT.");
    }

    @Test
    public void test08_Manual_WithCBR_MT() {
        runMeasurement(manualHierarchy, true, false, MT_THREADS, "08-Manual-WithCBR-MT.");
    }

    // =========================================================
    // Automatic BVH hierarchy measurements
    // =========================================================

    @Test
    public void test09_Auto_NoCBR_NoMT() {
        runMeasurement(autoHierarchy, false, true, 0, "09-Auto-NoCBR-NoMT.");
    }

    @Test
    public void test10_Auto_WithCBR_NoMT() {
        runMeasurement(autoHierarchy, true, true, 0, "10-Auto-WithCBR-NoMT.");
    }

    @Test
    public void test11_Auto_NoCBR_MT() {
        runMeasurement(autoHierarchy, false, true, MT_THREADS, "11-Auto-NoCBR-MT.");
    }

    @Test
    public void test12_Auto_WithCBR_MT() {
        runMeasurement(autoHierarchy, true, true, MT_THREADS, "12-Auto-WithCBR-MT.");
    }

    // =========================================================
    // Helper methods
    // =========================================================

    /**
     * Helper method that performs a single full measurement and prints the results.
     */
    private void runMeasurement(Geometries geometries, boolean cbr, boolean bvh,  int threads, String testName) {
        // Set the geometric structure for the scene
        scene.setGeometries(geometries);

        scene.setBvhTree(bvh);

        scene.setAABB(cbr);

        // Set multithreading
        cameraBuilder.setMultithreading(threads);

        long startTime = System.currentTimeMillis();

        Camera cameraOn = cameraBuilder
                .setRayTracer(scene, renderer.RayTracerType.SIMPLE)
                .build();

        cameraOn.renderImage();
        cameraOn.writeToImage(testName);

        long endTime = System.currentTimeMillis();
        System.out.println(">>> Render time for [" + testName + "]: " + (endTime - startTime) / 1000.0 + " seconds.");
    }

    private static Geometries buildSphereCluster(Point origin, int nx, int ny, int nz,
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
                    double r = radius * (0.85 + 0.3 * ((ix + iy + iz) % 3) / 3.0);
                    g.add(new Sphere(c, r).setEmission(emission).setMaterial(mat));
                }
            }
        }
        return g;
    }
}