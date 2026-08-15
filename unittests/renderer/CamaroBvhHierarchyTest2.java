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
import primitives.Double3;
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

    private static final int RESOLUTION = 400;
    private static final String RES_SUFFIX = "-" + RESOLUTION + "R";

    private static final double DELTA = 1e-6;

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
                        .setMaterial(new Material().setKD(0.1).setKS(0.2).setKR(0.15).setKG(15).setShininess(10).setKA(0.8)));

        Geometries spheres = new Geometries(
                // כדור אדום - Emission הונמך כדי לשמור על הצבע המקורי תחת התאורה החדשה
                new Sphere(new Point(-4, -2, 0.45), 0.45)
                        .setEmission(new Color(80, 10, 20))
                        .setMaterial(new Material()
                                .setKD(0.3)
                                .setKS(0.7).setShininess(50)
                                .setKT(0.4)
                                .setKB(0.9)),

                // כדור מרכזי - מראה (ללא שינוי, הוא לא פולט אור)
                new Sphere(new Point(40, 43, 0.70), 0.70)
                        .setMaterial(new Material()
                                .setKD(0.0)
                                .setKS(1).setShininess(300)
                                .setKR(1)
                                .setKT(0.0)
                                .setKG(2.0)),

                // כדור כחול - Emission הונמך משמעותית
                new Sphere(new Point(22, 20, 0.70), 0.70)
                        .setEmission(new Color(10, 30, 80))
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

// תאורת אווירה - משאירים באפור ניטרלי כדי לשמור על צללים רכים
        scene.setAmbientLight(new AmbientLight(new Color(60, 60, 60)));

// 1. תאורה מרכזית (Key Light) - חיזקנו ללבן מקסימלי והקטנו את מקדמי ההנחתה
// עכשיו האור הזה ישטוף את הרכב בעוצמה גבוהה וייתן לו מראה לבן ונקי
        scene.lights.add(new PointLight(new Color(255, 255, 255), new Point(-20, -24, 10))
                .setKl(0.001).setKq(0.0001));

// 2. תאורת מילוי רכה מהצד האחורי-ימני (שומרת על הנפח מאחור)
        scene.lights.add(new PointLight(new Color(100, 100, 100), new Point(0, 5, 10))
                .setKl(0.01).setKq(0.001));

        scene.lights.add(new PointLight(new Color(100, 100, 100), new Point(-10, -0, 5))
                .setKl(0.001).setKq(0.0001));

// 3. הזרקור הצהוב על הפגוש
// הורדנו מעט את עוצמת הצבע שלו כדי שיהווה "אקסנט" (הארה נקודתית) ולא ישתלט על כל הרכב
        scene.lights.add(new SpotLight(new Color(200, 180, 0), new Point(0, -6, 8), new Vector(0, 1, -2))
                .setKl(0.01).setKq(0.001).setNarrowBeam(60));

// 4. אור כיווני עדין - שינינו לאפור נקי כדי לתמוך בנפח הכללי בלי להוסיף צבעוניות
        scene.lights.add(new DirectionalLight(new Color(50, 50, 50), new Vector(1, -0.2, -1)));

        assertNotNull(scene, "Scene should not be null");

        // Set up camera
        cameraBuilder = Camera.getBuilder()
                // 1. Camera location: in front of the car (-30, -20) and slightly elevated (15)
                .setLocation(new Point(-20, -24, 3))

                // 2. View direction vector (To-Vector):
                .setDirection(Point.ZERO, Vector.AXIS_Z)

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
     * Loads geometries from a ZIP file containing a JSON scene description.
     *
     * @param filePath path to the ZIP file containing Home.json
     * @return a {@link Geometries} object populated with all parsed shapes
     */
    public static Geometries loadGeometriesFromJson(String filePath) {
        Geometries geometries = new Geometries();
        Map<String, Material> materialsMap = new HashMap<>();
        Map<String, Color> colorsMap = new HashMap<>();
        Map<String, Color> emissionsMap = new HashMap<>();

        try (ZipFile zipFile = new ZipFile(filePath)) {
            ZipEntry entry = zipFile.getEntry("Home.json");

            if (entry == null) {
                throw new RuntimeException("The file Home.json was not found inside the ZIP.");
            }

            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                String content = new String(inputStream.readAllBytes());
                JSONObject root = new JSONObject(content);

                parseMaterials(root, materialsMap, colorsMap, emissionsMap);
                parseSpheres(root, geometries, materialsMap);
                parsePlanes(root, geometries, materialsMap);
                parseTriangles(root, geometries, materialsMap, colorsMap, emissionsMap);
            }
        } catch (Exception e) {
            System.err.println("Error reading JSON scene file: " + e.getMessage());
            e.printStackTrace();
        }

        return geometries;
    }

    private static void parseMaterials(JSONObject root, Map<String, Material> materialsMap, Map<String, Color> colorsMap, Map<String, Color> emissionsMap) {
        if (!root.has("materials")) return;

        JSONObject materialsObj = root.getJSONObject("materials");
        for (String matName : materialsObj.keySet()) {
            JSONObject m = materialsObj.getJSONObject(matName);

            // --- קריאת צבע בסיס (RGB נפרדים לשימוש בחומר) ---
            JSONArray col = m.getJSONArray("color");
            double r = col.getDouble(0);
            double g = col.getDouble(1);
            double b = col.getDouble(2);
            Color baseColor = new Color(r * 255, g * 255, b * 255);

            // --- קריאת Emission ---
            Color emissionColor = new Color(0, 0, 0); // ברירת מחדל: לא מאיר
            if (m.has("emission")) {
                JSONArray em = m.getJSONArray("emission");
                emissionColor = new Color(
                        em.getDouble(0) * 255,
                        em.getDouble(1) * 255,
                        em.getDouble(2) * 255
                );
            }

            double kt = m.optDouble("kt", 0.0);
            double roughness = m.optDouble("roughness", 0.5);

            // --- התיקון: שימוש ב-Double3 כדי לשמור על הצבע המקורי ---
            // נכפיל בפקטור (למשל 0.8) כדי לפנות מקום להשתקפויות (Specular)
            double diffuseFactor = 0.8;
            Double3 kD = new Double3(r * diffuseFactor, g * diffuseFactor, b * diffuseFactor);

            // הברקות (Specular) נשארות לבנות/אפורות כדי לשקף את צבע התאורה
            double ks = 1.0 - roughness;
            Double3 kS = new Double3(ks, ks, ks);

            double kg = m.optDouble("kg", roughness);
            double kb = m.optDouble("kb", roughness);

            Material mat = new Material()
                    .setKD(kD) // משתמשים בצבע במקום במספר בודד!
                    .setKS(kS)
                    .setKA(new Double3(r * 0.1, g * 0.1, b * 0.1)) // תאורת אווירה בצבע תואם
                    .setShininess((int) (ks * 100))
                    .setKT(kt)
                    .setKG(kg)
                    .setKB(kb);

            materialsMap.put(matName, mat);
            colorsMap.put(matName, baseColor);
            emissionsMap.put(matName, emissionColor);
        }
    }

    private static void parseSpheres(JSONObject root, Geometries geometries, Map<String, Material> materialsMap) {
        if (!root.has("spheres")) return;

        JSONArray spheres = root.getJSONArray("spheres");
        for (int i = 0; i < spheres.length(); i++) {
            JSONObject s = spheres.getJSONObject(i);
            JSONArray c = s.getJSONArray("center");

            Point center = new Point(c.getDouble(0), c.getDouble(1), c.getDouble(2));
            Sphere sphere = new Sphere(center, s.getDouble("radius"));

            String matName = s.optString("material", "");
            if (materialsMap.containsKey(matName)) {
                sphere.setMaterial(materialsMap.get(matName));
            }
            geometries.add(sphere);
        }
    }

    private static void parsePlanes(JSONObject root, Geometries geometries, Map<String, Material> materialsMap) {
        if (!root.has("planes")) return;

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
    }

    private static void parseTriangles(JSONObject root, Geometries geometries, Map<String, Material> materialsMap, Map<String, Color> colorsMap , Map<String, Color> emissionsMap) {
        if (!root.has("triangles")) return;

        JSONArray triangles = root.getJSONArray("triangles");
        for (int i = 0; i < triangles.length(); i++) {
            JSONObject t = triangles.getJSONObject(i);
            JSONArray v0 = t.getJSONArray("v0");
            JSONArray v1 = t.getJSONArray("v1");
            JSONArray v2 = t.getJSONArray("v2");

            Point p0 = new Point(v0.getDouble(0), v0.getDouble(1), v0.getDouble(2));
            Point p1 = new Point(v1.getDouble(0), v1.getDouble(1), v1.getDouble(2));
            Point p2 = new Point(v2.getDouble(0), v2.getDouble(1), v2.getDouble(2));

            // Skip degenerate or near-degenerate triangles.
            if (p0.distance(p1) < DELTA || p1.distance(p2) < DELTA || p2.distance(p0) < DELTA) continue;

            try {
                Vector vec1 = p1.subtract(p0);
                Vector vec2 = p2.subtract(p0);
                if (vec1.crossProduct(vec2).length() < DELTA) continue; // Collinear points.
            } catch (IllegalArgumentException e) {
                continue;
            }

            Triangle triangle = new Triangle(p0, p1, p2);
            String matName = t.optString("material", "");

            if (materialsMap.containsKey(matName)) {
                triangle.setMaterial(materialsMap.get(matName));
                triangle.setEmission(emissionsMap.getOrDefault(matName, new Color(0, 0, 0)));
            }
            geometries.add(triangle);
        }
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