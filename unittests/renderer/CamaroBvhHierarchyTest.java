package renderer;

import geometries.api.Intersectable;
import geometries.impl.Geometries;
import geometries.impl.Plane;
import geometries.impl.Triangle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import scene.Scene;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit test for rendering the Camaro scene with a structured BVH hierarchy,
 * utilizing specific materials and colors mapped from the OBJ file.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CamaroBvhHierarchyTest {

    private static Scene scene;
    private static Camera.Builder cameraBuilder;

    private static Geometries flatScene;
    private static Geometries manualHierarchy;
    private static Geometries autoHierarchy;

    private static final int MT_THREADS = -1; // מספר תהליכונים אופטימלי

    // מפה לשמירת זמני הריצה של כל הטסטים כדי להדפיס אותם במרוכז בסוף
    private static final Map<String, Double> renderTimes = new LinkedHashMap<>();

    @BeforeAll
    public static void setupScene() {
        // 1. יצירת אובייקטי Point
        Point[] points = new Point[CamaroData.RAW_VERTICES.length / 3];
        for (int i = 0; i < CamaroData.RAW_VERTICES.length; i += 3) {
            points[i / 3] = new Point(
                    CamaroData.RAW_VERTICES[i],
                    CamaroData.RAW_VERTICES[i + 1],
                    CamaroData.RAW_VERTICES[i + 2]
            );
        }

        // 2. בניית היררכיה ידנית (Manual) והחלת החומרים מהמערך
        manualHierarchy = new Geometries();
        Geometries currentCarChunk = new Geometries();
        int trianglesCount = 0;
        int faceIndex = 0; // עוקב אחרי האינדקס במערך החומרים של הפרצופים

        for (int i = 0; i < CamaroData.RAW_INDICES.length; i += 3) {
            Point p1 = points[CamaroData.RAW_INDICES[i]];
            Point p2 = points[CamaroData.RAW_INDICES[i + 1]];
            Point p3 = points[CamaroData.RAW_INDICES[i + 2]];

            if (!p1.equals(p2) && !p1.equals(p3) && !p2.equals(p3)) {

                // שליפת מזהה החומר למשולש הספציפי הזה
                int materialId = CamaroData.RAW_FACE_MATERIALS[faceIndex];

                Color faceColor = new Color(100, 100, 100); // ברירת מחדל אפורה
                Material faceMaterial = new Material().setKD(0.5).setKS(0.5).setShininess(50);

                // הגדרת המאפיינים הפיזיקליים בהתאם למזהה שהתקבל מה-OBJ
                switch (materialId) {
                    case 0: // Lataria - פח הרכב
                        faceColor = new Color(2, 2, 2); // שחור עמוק עם נגיעה כחלחלה למראה יוקרתי
                        faceMaterial = new Material().setKD(0.05).setKS(0.9).setShininess(500).setKR(0.25); // KR מוסיף השתקפות חלשה של הסביבה

                        break;
                    case 1: // Chroma - כרום/מתכת
                        faceColor = new Color(20, 20, 20);
                        faceMaterial = new Material().setKD(0.2).setKS(0.8).setShininess(300).setKR(0.8);
                        break;
                    case 2: // Vidro - חלונות/זכוכית
                        faceColor = new Color(10, 10, 10);
                        faceMaterial = new Material().setKD(0.1).setKS(0.9).setShininess(300).setKT(0.8).setKR(0.1);
                        break;
                    case 3: // Material.002 - חלקי פנים וכו'
                        faceColor = new Color(40, 40, 40);
                        faceMaterial = new Material().setKD(0.6).setKS(0.4).setShininess(60);
                        break;
                    case 4: // Farol.001 - כיסוי הפנסים
                        faceColor = new Color(200, 200, 200);
                        faceMaterial = new Material().setKD(0.2).setKS(0.8).setShininess(200).setKT(0.6);
                        break;
                    case 5: // Lamp - המנורה עצמה (פנסים דולקים)
                        faceColor = new Color(255, 255, 255); // לבן בוהק אבסולוטי
                        // נאפס KD ו-KS כי פנס דולק לא מושפע מאור חיצוני, הוא רק פולט אור
                        faceMaterial = new Material().setKD(0.0).setKS(0.0).setShininess(0);

                        break;
                    case 6: // Pneu - צמיגי גומי
                        faceColor = new Color(15, 15, 15);
                        faceMaterial = new Material().setKD(0.8).setKS(0.1).setShininess(10);
                        break;
                    case 7: // Aro - ג'אנטים (חישוקים) ואלמנטים נוספים
                        faceColor = new Color(20, 20, 20);
                        faceMaterial = new Material().setKD(0.1).setKS(0.9).setShininess(300).setKR(0.7);
                        break;
                    case 8: // Material - כללי
                        faceColor = new Color(100, 100, 100);
                        faceMaterial = new Material().setKD(0.5).setKS(0.5).setShininess(50);
                        break;
                    case 9: // Preto - פלסטיק שחור (גריל, פגושים תחתונים)
                        faceColor = new Color(20, 20, 20);
                        faceMaterial = new Material().setKD(0.6).setKS(0.4).setShininess(80);
                        break;
                }

                currentCarChunk.add(new Triangle(p1, p2, p3)
                        .setEmission(faceColor)
                        .setMaterial(faceMaterial));

                trianglesCount++;

                if (trianglesCount >= 500) {
                    manualHierarchy.add(currentCarChunk);
                    currentCarChunk = new Geometries();
                    trianglesCount = 0;
                }
            }
            faceIndex++;
        }

        if (trianglesCount > 0) {
            manualHierarchy.add(currentCarChunk);
        }

        Geometries backgroundBranch = new Geometries(
                // רצפה: נשארת עם ההשתקפות (KR) היפה, אבל כהה כדי שלא תישרף
                new Plane(new Point(0, -1, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(130, 130, 130))
                        .setMaterial(new Material().setKD(0.1).setKS(0.2).setShininess(10).setKR(0.4))

                // קיר אחורי (ימין של התמונה): צבע קרם אבל מונמך מאוד. האור החזק יבהיר אותו.
                // מיקום ב- Z = -8 כדי שיהיה קרוב וייצור זווית ישרה אמיתית
               /* new Plane(new Point(0, 0, -8), new Vector(0, 0, 1))
                        .setEmission(new Color(90, 75, 65))
                        .setMaterial(new Material().setKD(0.15).setKS(0)),*/

                // קיר שמאלי (שמאל של התמונה): צבע אפור, מונמך מאוד.
                // מיקום ב- X = -8, חותך את הקיר הקודם ב-90 מעלות
              /*  new Plane(new Point(-8, 0, 0), new Vector(1, 0, 0))
                        .setEmission(new Color(90, 75, 65))
                        .setMaterial(new Material().setKD(0.15).setKS(0))*/
        );
        manualHierarchy.add(backgroundBranch);

        flatScene = manualHierarchy.flatten();
        autoHierarchy = manualHierarchy.flatten();

        long startTreeTime = System.currentTimeMillis();
        long endTreeTime = System.currentTimeMillis();
        System.out.println("--- Overhead: Auto BVH Tree built in " + (endTreeTime - startTreeTime) / 1000.0 + " seconds ---");

        scene = new Scene("Camaro Test Scene")
                .setBackground(new Color(100, 100, 144)); // רקע אפלולי

// 1. תאורת מפתח (Key Light) ישירה מלמעלה - מאירה את הרכב חזק, מונעת צללים ארוכים הצידה
        scene.lights.add(new lighting.impl.SpotLight(new Color(600, 600, 600), new Point(0, 150, 0), new Vector(0, -1, 0))
                .setKl(2E-5).setKq(1E-6));

// 2. תאורת מילוי מקיפה כדי לבטל את שאריות הצל הצידה ולהאיר את הפלסטיקה השחורה
        scene.lights.add(new lighting.impl.PointLight(new Color(120, 120, 120), new Point(100, 100, 100))
                .setKl(0.0001).setKq(0.00005));

// 3. תאורה אחורית להדגשת קווי המתאר של הגג
        scene.lights.add(new lighting.impl.PointLight(new Color(80, 80, 120), new Point(-50, 100, -150))
                .setKl(0.0001).setKq(0.00005));

// 4. אלומות אור מהפנסים הקדמיים של הרכב (מדמה זריקת אור קדימה)
        scene.lights.add(new lighting.impl.SpotLight(new Color(800, 800, 800), new Point(-2, 3, 10), new Vector(0, -0.1, 1))
                .setKl(1E-4).setKq(1E-5));
        scene.lights.add(new lighting.impl.SpotLight(new Color(800, 800, 800), new Point(2, 3, 10), new Vector(0, -0.1, 1))
                .setKl(1E-4).setKq(1E-5));
        assertNotNull(scene, "Scene should not be null");

        cameraBuilder = Camera.getBuilder()
                .setLocation(new Point(3, 2, 3))
                .setDirection(new Vector(-3, -2, -3), new Vector(-1, 3, -1))
                .setVpSize(200, 200)
                .setVpDistance(100)
                .setUseAdvancedEffects(true)
                .setSampler(new sampling.impl.JitteredSampler())
                .setRaysAmount(9)
                .setDebugPrint(0.1)
                .setResolution(300, 300); // הורדתי מעט כדי שהטסטים יסתיימו מהר
    }

    // =========================================================
    // מדידות סצנה משוטחת (Flat)
    // =========================================================

    @Test
    public void test01_Flat_NoCBR_NoMT() {
        runMeasurement(flatScene, false,false, 0, "camaro-01-Flat-NoCBR-NoMT");
    }

    @Test
    public void test02_Flat_WithCBR_NoMT() {
        runMeasurement(flatScene, true,false, 0, "camaro-02-Flat-WithCBR-NoMT");
    }

    @Test
    public void test03_Flat_NoCBR_MT() {
        runMeasurement(flatScene, false,false, MT_THREADS, "camaro-03-Flat-NoCBR-MT");
    }

    @Test
    public void test04_Flat_WithCBR_MT() {
        runMeasurement(flatScene, true,false, MT_THREADS, "camaro-04-Flat-WithCBR-MT");
    }

    // =========================================================
    // מדידות היררכיה ידנית (Manual BVH)
    // =========================================================

    @Test
    public void test05_Manual_NoCBR_NoMT() {
        runMeasurement(manualHierarchy, false,false, 0, "camaro-05-Manual-NoCBR-NoMT");
    }

    @Test
    public void test06_Manual_WithCBR_NoMT() {
        runMeasurement(manualHierarchy, true,false, 0, "camaro-06-Manual-WithCBR-NoMT");
    }

    @Test
    public void test07_Manual_NoCBR_MT() {
        runMeasurement(manualHierarchy, false,false, MT_THREADS, "camaro-07-Manual-NoCBR-MT");
    }

    @Test
    public void test08_Manual_WithCBR_MT() {
        runMeasurement(manualHierarchy, true,false, MT_THREADS, "camaro-08-Manual-WithCBR-MT");
    }

    // =========================================================
    // מדידות היררכיה אוטומטית (Auto BVH)
    // =========================================================

    @Test
    public void test09_Auto_NoCBR_NoMT() {
        runMeasurement(autoHierarchy, false, true, 0, "camaro-09-Auto-NoCBR-NoMT");
    }

    @Test
    public void test10_Auto_WithCBR_NoMT() {
        runMeasurement(autoHierarchy, true, true,0, "camaro-10-Auto-WithCBR-NoMT");
    }

    @Test
    public void test11_Auto_NoCBR_MT() {
        runMeasurement(autoHierarchy, false,true, MT_THREADS, "camaro-11-Auto-NoCBR-MT");
    }

    @Test
    public void test12_Auto_WithCBR_MT() {
        runMeasurement(autoHierarchy, true,true, MT_THREADS, "camaro-12-Auto-WithCBR-MT");
    }

    // =========================================================
    // מתודות עזר והדפסת סיכום בסוף
    // =========================================================

    private void runMeasurement(Geometries geometries, boolean useCbr,boolean bvh, int threads, String testName) {
        scene.setGeometries(geometries);
        scene.setAABB(useCbr);
        scene.setBvhTree(bvh);
        cameraBuilder.setMultithreading(threads);

        long startTime = System.currentTimeMillis();

        Camera cameraOn = cameraBuilder
                .setRayTracer(scene, renderer.RayTracerType.SIMPLE)
                .build();

        cameraOn.renderImage();
        cameraOn.writeToImage(testName);

        long endTime = System.currentTimeMillis();
        double timeInSeconds = (endTime - startTime) / 1000.0;

        System.out.println(">>> Render time for [" + testName + "]: " + timeInSeconds + " seconds.");

        // שומרים את הזמן בתוך המפה כדי שנוכל להדפיס בסוף
        renderTimes.put(testName, timeInSeconds);
    }

    /**
     * פונקציה זו תרוץ באופן אוטומטי ברגע שכל 12 הטסטים יסתיימו,
     * ותדפיס טבלה מרוכזת ויפה של כל הזמנים!
     */
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