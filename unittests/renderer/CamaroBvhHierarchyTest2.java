package renderer;

import geometries.impl.Geometries;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.impl.AmbientLight;
import lighting.impl.DirectionalLight;
import lighting.impl.PointLight;
import lighting.impl.SpotLight;
import org.json.JSONArray;
import org.json.JSONObject;
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

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit test for rendering the scene directly from JSON with a structured BVH hierarchy,
 * utilizing materials and geometries loaded directly inside the test.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CamaroBvhHierarchyTest2 {

    private static Scene scene;
    private static Camera.Builder cameraBuilder;

    private static Geometries flatScene;
    private static Geometries manualHierarchy;
    private static Geometries autoHierarchy;

    private static final int MT_THREADS = -1; // optimal thread count

    private static final int RESOLUTION = 2006;
    private static final String RES_SUFFIX = "-" + RESOLUTION + "R";

    // Map to store render times for all tests
    private static final Map<String, Double> renderTimes = new LinkedHashMap<>();

    @BeforeAll
    public static void setupScene() {
        // 1. Path to the JSON file
        String jsonPath = "unittests/renderer/MercedesData.zip";

        // 2. Load geometries directly from JSON without ModelLoader
        Geometries loadedCarModel = loadGeometriesFromJson(jsonPath);
        Geometries planeScena = new Geometries(
                new Plane(new Point(1, 0, 0), new Vector(0, 0, 1))
                        .setEmission(new Color(130, 130, 130))
                        .setMaterial(new Material().setKD(0.1).setKS(0.2).setKR(0.5).setKG(15).setShininess(10).setKA(0.8)));

        Geometries spheres = new Geometries(

                new Sphere(new Point(-1, -2, 0.3), 0.3)
                        .setEmission(new Color(220, 20, 40))
                        .setMaterial(new Material()
                                .setKD(0.3)
                                .setKS(0.7).setShininess(50)
                                .setKT(0.9)
                                .setKB(0.9)),
                //  (Glossy Reflection)
                new Sphere(new Point(40, 43, 0.70), 0.70)
                        //.setEmission(new Color(20, 20, 20))
                        .setMaterial(new Material()
                                .setKD(0.0)
                                .setKS(1).setShininess(300)
                                .setKR(1)
                                .setKT(0.0)
                                .setKG(2.0)),

                new Sphere(new Point(22, 20, 0.70), 0.70)
                        .setEmission(new Color(30, 100, 200))
                        .setMaterial(new Material().setKD(0.2).setKS(0.8).setShininess(200).setKT(0.0))
        );

        Geometries fullScene = new Geometries(loadedCarModel, planeScena, spheres);

        manualHierarchy = fullScene;
        flatScene = manualHierarchy.flatten();

        autoHierarchy = manualHierarchy.flatten();

        long startBuild = System.currentTimeMillis();
        autoHierarchy.buildBvhTree();
        long endBuild = System.currentTimeMillis();

        double buildTimeSec = (endBuild - startBuild) / 1000.0;
        System.out.println("==================================================");
        System.out.println(">>> Automatic BVH Tree Build Time: " + buildTimeSec + " seconds.");
        System.out.println("==================================================");

        scene = new Scene("Direct JSON Scene");
        scene.setBackground(new Color(100, 100, 100));
        scene.setAmbientLight(new AmbientLight(new Color(30, 30, 30)));

        // Lights
        scene.lights.add(new PointLight(new Color(250, 250, 250), new Point(0, 30, 0))
                .setKl(0.001).setKq(0.0001));

        scene.lights.add(new SpotLight(new Color(400, 400, 0), new Point(-1, -2, 4), new Vector(1, 0.9, -1))
                .setKl(0.001).setKq(0.0001).setNarrowBeam(19));

        scene.lights.add(new PointLight(new Color(50, 100, 200), new Point(40, 43, 15))
                .setKl(0.001).setKq(0.0001));

        scene.lights.add(new PointLight(new Color(100, 100, 100), new Point(11, 15, 5))
                .setKl(0.001).setKq(0.0001));

        scene.lights.add(new DirectionalLight(new Color(100, 80, 100), new Vector(-1, -0.9, -0.05)));

        assertNotNull(scene, "Scene should not be null");

        // Set up camera
        cameraBuilder = Camera.getBuilder()
                // 1. Camera location: in front of the car (-30, -20) and slightly elevated (15)
                .setLocation(new Point(-22, -22, 4))

                // 2. View direction vector (To-Vector):
                .setDirection(new Vector(25, 26, -2), new Vector(0, 0, 1))

                .setVpSize(18, 18)
                .setVpDistance(80)
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setSamplerShape(TargetShapeType.CIRCLE)
                .setRaysAmount(9)
                .setDebugPrint(0.5)
                .setResolution(RESOLUTION, RESOLUTION);
    }

    /**
     * Internal function to load geometries directly from JSON and build Geometries.
     */
    private static Geometries loadGeometriesFromJson(String filePath) {
        Geometries geometries = new Geometries();
        Map<String, Material> materialsMap = new HashMap<>();
        Map<String, Color> colorsMap = new HashMap<>();

        try (ZipFile zipFile = new ZipFile(filePath)) {

            ZipEntry entry = zipFile.getEntry("Home.json");

            if (entry == null) {
                throw new RuntimeException("The file Home.json was not found inside the ZIP.");
            }

            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                String content = new String(inputStream.readAllBytes());
                JSONObject root = new JSONObject(content);

                if (root.has("materials")) {
                    JSONObject materialsObj = root.getJSONObject("materials");
                    for (String matName : materialsObj.keySet()) {
                        JSONObject m = materialsObj.getJSONObject(matName);

                        JSONArray col = m.getJSONArray("color");
                        Color baseColor = new Color(
                                col.getDouble(0) * 255,
                                col.getDouble(1) * 255,
                                col.getDouble(2) * 255
                        );

                        // Extract KT (transparency) from JSON
                        double kt = m.optDouble("kt", 0.0);

                        // If the material name contains "glass" and kt is still 0, set a default transparency
                        if (matName.toLowerCase().contains("glass") && kt == 0.0) {
                            kt = 0.85; // 85% transparency for glass
                        }
                        double roughness = m.optDouble("roughness", 0.5);

// Lower roughness → smaller Kd, larger Ks for a shiny look
                        double kd = roughness;
                        double ks = 1.0 - roughness;

                        Material mat = new Material()
                                .setKD(kd)
                                .setKS(ks)
                                .setShininess((int) ((1.0 - roughness) * 100))
                                .setKT(kt);

                        materialsMap.put(matName, mat);
                        colorsMap.put(matName, baseColor);
                    }
                }

                // B. Load spheres
           /* if (root.has("spheres")) {
                JSONArray spheres = root.getJSONArray("spheres");
                for (int i = 0; i < spheres.length(); i++) {
                    JSONObject s = spheres.getJSONObject(i);
                    JSONArray c = s.getJSONArray("center");
                    Point center = new Point(c.getDouble(0), c.getDouble(1), c.getDouble(2));
                    double radius = s.getDouble("radius");

                    Sphere sphere = new Sphere(center, radius);
                    String matName = s.optString("material", "");
                    if (materialsMap.containsKey(matName)) {
                        sphere.setMaterial(materialsMap.get(matName));
                    }
                    geometries.add(sphere);
                }
            }*/

                // C. Load planes
           /* if (root.has("planes")) {
                JSONArray planes = root.getJSONArray("planes");
                for (int i = 0; i < planes.length(); i++) {
                    JSONObject p = planes.getJSONObject(i);
                    JSONArray pt = p.getJSONArray("point");
                    JSONArray norm = p.getJSONArray("normal");

                    Point point = new Point(pt.getDouble(0), pt.getDouble(1), pt.getDouble(2));
                    Vector normal = new Vector(norm.getDouble(0), norm.getDouble(1), norm.getDouble(2));

                    Plane plane = new Plane(point, normal);
                    String matName = p.optString("material", "");
                    if (materialsMap.containsKey(matName)) {
                        plane.setMaterial(materialsMap.get(matName));
                    }
                    geometries.add(plane);
                }
            }*/

// D. Load triangles
                if (root.has("triangles")) {
                    JSONArray triangles = root.getJSONArray("triangles");

                    // Set a small delta for floating-point comparison
                    final double EPSILON = 0.000001;

                    for (int i = 0; i < triangles.length(); i++) {
                        JSONObject t = triangles.getJSONObject(i);
                        JSONArray v0 = t.getJSONArray("v0");
                        JSONArray v1 = t.getJSONArray("v1");
                        JSONArray v2 = t.getJSONArray("v2");

                        Point p0 = new Point(v0.getDouble(0), v0.getDouble(1), v0.getDouble(2));
                        Point p1 = new Point(v1.getDouble(0), v1.getDouble(1), v1.getDouble(2));
                        Point p2 = new Point(v2.getDouble(0), v2.getDouble(1), v2.getDouble(2));

                        // 1. Check distance between points with delta (before creating the triangle!)
                        if (p0.distance(p1) < EPSILON || p1.distance(p2) < EPSILON || p2.distance(p0) < EPSILON) {
                            continue; // points almost coincide - skip immediately
                        }

                        // 2. Collinearity check (are the points nearly on the same line) with delta
                        // v1 = p1 - p0, v2 = p2 - p0
                        try {
                            Vector vec1 = p1.subtract(p0);
                            Vector vec2 = p2.subtract(p0);

                            // If the cross product is near zero, the points are collinear
                            Vector cross = vec1.crossProduct(vec2);
                            if (cross.length() < EPSILON) {
                                continue; // skip degenerate triangle on a line
                            }
                        } catch (IllegalArgumentException e) {
                            // Catches the case where p1-p0 or p2-p0 produced a zero vector
                            continue;
                        }
                        Triangle triangle = new Triangle(p0, p1, p2);
                        String matName = t.optString("material", "");

                        if (materialsMap.containsKey(matName)) {
                            triangle.setMaterial(materialsMap.get(matName));
                            triangle.setEmission(colorsMap.get(matName)); // <-- critical: sets the visible color!
                        }
                        geometries.add(triangle);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error reading JSON scene file: " + e.getMessage());
            e.printStackTrace();
        }

        return geometries;
    }
// =========================================================
    //flat
// =========================================================

    @Test
    public void test01_Flat_NoCBR_NoMT() {
        runMeasurement(flatScene, false, false, 0, "MERCEDES-01-Flat-NoCBR-NoMT");
    }

    @Test
    public void test02_Flat_WithCBR_NoMT() {
        runMeasurement(flatScene, true, false, 0, "MERCEDES-02-Flat-WithCBR-NoMT");
    }

    @Test
    public void test03_Flat_NoCBR_MT() {
        runMeasurement(flatScene, false, false, MT_THREADS, "MERCEDES-03-Flat-NoCBR-MT");
    }

    @Test
    public void test04_Flat_WithCBR_MT() {
        runMeasurement(flatScene, true, false, MT_THREADS, "MERCEDES-04-Flat-WithCBR-MT");
    }

    // =========================================================
    //  (Manual BVH)
    // =========================================================

    @Test
    public void test05_Manual_NoCBR_NoMT() {
        runMeasurement(manualHierarchy, false, false, 0, "MERCEDES-05-Manual-NoCBR-NoMT");
    }

    @Test
    public void test06_Manual_WithCBR_NoMT() {
        runMeasurement(manualHierarchy, true, false, 0, "MERCEDES-06-Manual-WithCBR-NoMT");
    }

    @Test
    public void test07_Manual_NoCBR_MT() {
        runMeasurement(manualHierarchy, false, false, MT_THREADS, "MERCEDES-07-Manual-NoCBR-MT");
    }

    @Test
    public void test08_Manual_WithCBR_MT() {
        runMeasurement(manualHierarchy, true, false, MT_THREADS, "MERCEDES-08-Manual-WithCBR-MT");
    }

    // =========================================================
    //  (Auto BVH)
    // =========================================================

    @Test
    public void test09_Auto_NoCBR_NoMT() {
        runMeasurement(autoHierarchy, false, true, 0, "MERCEDES-09-Auto-NoCBR-NoMT");
    }

    @Test
    public void test10_Auto_WithCBR_NoMT() {
        runMeasurement(autoHierarchy, true, true, 0, "MERCEDES-10-Auto-WithCBR-NoMT");
    }

    @Test
    public void test11_Auto_NoCBR_MT() {
        runMeasurement(autoHierarchy, false, true, MT_THREADS, "MERCEDES-11-Auto-NoCBR-MT");
    }

    @Test
    public void test12_Auto_WithCBR_MT() {
        runMeasurement(autoHierarchy, true, true, MT_THREADS, "MERCEDES-12-Auto-WithCBR-MT");
    }

    @Test
    public void test13_Auto_WithCBR_MT_NoEffects() {

        cameraBuilder.setUseAdvancedEffects(false);

        runMeasurement(autoHierarchy, true, true, MT_THREADS, "MERCEDES-13-Auto-WithCBR-MT-NoEffects");

        cameraBuilder.setUseAdvancedEffects(true);
    }

    private void runMeasurement(Geometries geometries, boolean useCbr, boolean bvh, int threads, String testName) {
        scene.setGeometries(geometries);
        scene.setAABB(useCbr);
        scene.setBvhTree(bvh);
        cameraBuilder.setMultithreading(threads);

        String finalTestName = testName + RES_SUFFIX;

        long startTime = System.currentTimeMillis();

        Camera cameraOn = cameraBuilder
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

        cameraOn.renderImage();
        cameraOn.writeToImage(finalTestName);

        long endTime = System.currentTimeMillis();
        double timeInSeconds = (endTime - startTime) / 1000.0;

        System.out.println(">>> Render time for [" + finalTestName + "]: " + timeInSeconds + " seconds.");
        renderTimes.put(finalTestName, timeInSeconds);
    }

    @org.junit.jupiter.api.AfterAll
    public static void printSummary() {
        System.out.println("\n==================================================");
        System.out.println("             SUMMARY OF RENDER TIMES              ");
        System.out.println("==================================================");
        renderTimes.forEach((name, time) ->
                System.out.printf("%-30s : %7.3f seconds%n", name, time)
        );
        System.out.println("==================================================\n");
    }
}