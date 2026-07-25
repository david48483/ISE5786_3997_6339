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
 * Unit test for rendering a scene using the BVH (Bounding Volume Hierarchy) acceleration structure.
 * This test uses a flat list of geometries, allowing the automated SAH (Surface Area Heuristic)
 * builder to construct the optimal tree dynamically before rendering.
 */
public class BvhTreeTest {

    /**
     * Default constructor for BvhHierarchyTest.
     */
    public BvhTreeTest() {
    }

    /**
     * Tests rendering with a flat geometries structure and BVH acceleration enabled.
     */
    @Test
    public void testBvhHierarchyStructure() {

        // The root container that will hold all geometries in a flat structure.
        Geometries rootGeometries = new Geometries();

        // 1. Add infinite planes directly to the root.
        // The BVH builder will identify these (null bounding box) and place them in the main branch.
        rootGeometries.add(
                new Plane(new Point(0, -70, 0), Vector.AXIS_Y)
                        .setMaterial(new Material().setKD(0.2).setKS(0.2).setShininess(30).setKR(1).setKG(7.0)),
                new Plane(new Point(0, 0, -200), Vector.AXIS_Z)
                        .setMaterial(new Material().setKD(0.5).setKS(0.1).setShininess(10))
        );

        // Define sample materials for the sphere clusters: glossy, glass-like, and milky.
        Material glossyMat = new Material().setKD(0.2).setKS(0.8).setShininess(200).setKR(0.6).setKG(4.0);
        Material glassyMat = new Material().setKD(0.2).setKS(0.5).setShininess(120).setKT(0.85);
        Material milkyMat = new Material().setKD(0.4).setKS(0.2).setShininess(40).setKT(0.5).setKB(80.0);

        // 2. Add multiple dense clusters of spheres directly to the root container.
        buildSphereCluster(rootGeometries, new Point(-80, -10, -90), 4, 4, 1, 14, 6, glossyMat, new Color(30, 80, 30));
        buildSphereCluster(rootGeometries, new Point(85, -5, -95), 4, 4, 1, 14, 6, glassyMat, new Color(80, 30, 30));
        buildSphereCluster(rootGeometries, new Point(0, 35, -110), 4, 4, 1, 14, 5.5, milkyMat, new Color(30, 30, 80));

        // 3. Add finite geometries (a group of triangles/pyramids) to the root container.
        Point p1 = new Point(-40, -69, -30);
        Point p2 = new Point(-20, -69, -30);
        Point p3 = new Point(-30, -69, -50);
        Point pTop = new Point(-30, -45, -40);
        Material pyramidMat = new Material().setKD(0.5).setKS(0.5).setShininess(60).setKR(0.2).setKG(3.0);

        rootGeometries.add(
                new Triangle(p1, p2, pTop).setMaterial(pyramidMat),
                new Triangle(p2, p3, pTop).setMaterial(pyramidMat),
                new Triangle(p3, p1, pTop).setMaterial(pyramidMat),
                new Triangle(p1, p2, p3).setMaterial(pyramidMat)
        );

        // Add a few more minor details (small triangles)
        rootGeometries.add(
                new Triangle(new Point(15, -69, -60), new Point(25, -69, -60), new Point(20, -60, -50))
                        .setMaterial(new Material().setKD(0.5).setKS(0.2).setShininess(30).setKR(0.1)),
                new Triangle(new Point(35, -69, -60), new Point(45, -69, -60), new Point(40, -58, -48))
                        .setMaterial(new Material().setKD(0.5).setKS(0.2).setShininess(30).setKT(0.3))
        );

        // 4. Initialize the scene and lights
        Scene scene = new Scene("BVH Hierarchy Test Scene");
        scene.lights.add(new SpotLight(new Color(700, 400, 400), new Point(60, 50, 100), new Vector(-1, -1, -3))
                .setKl(4E-4).setKq(2E-5));

        scene.lights.add(new PointLight(new Color(500, 250, 250), new Point(-60, -50, 100))
                .setKl(0.0005).setKq(0.0005));

        scene.lights.add(new DirectionalLight(new Color(200, 200, 200), new Vector(1, -1, -1)));

        // Pass the flat root geometries object to the scene.
        // The SAH tree will be built from this flat list upon camera build if enabled.
        scene.setGeometries(rootGeometries);

        assertNotNull(scene, "Scene should not be null");

        // 5. Configure the Camera
        Camera.Builder cameraBuilder = Camera.getBuilder()
                .setLocation(new Point(0, 0, 500))
                .setDirection(new Vector(0, 0, -1), Vector.AXIS_Y)
                .setVpSize(200, 200)
                .setVpDistance(500)
                .setDebugPrint(0.3)
                .setSamplerShape(TargetShapeType.CIRCLE)
                .setResolution(800, 800);

        // 6. Run performance tests across different configurations

        // Test configuration 1: AABB off, Tree off (Brute force)
        scene.setAABB(false);
        scene.setBvhTree(false);
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(-2)
                .setRaysAmount(9), "BVH-50plus-noBVH-2");

        // Test configuration 2: AABB on, Tree off (Flat bounding boxes)
        scene.setAABB(true);
        scene.setBvhTree(false);
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(-2)
                .setRaysAmount(9), "BVH-50plus-AABB-only-2");

        // Test configuration 3: Full BVH Tree optimization (AABB must be managed internally by the builder)
        scene.setAABB(false);
        scene.setBvhTree(true);
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(-2)
                .setRaysAmount(9), "BVH-50plus-BVH-TREE-2");

        // Additional thread testing with BVH Tree enabled
        scene.setBvhTree(true);
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(-1)
                .setRaysAmount(9), "BVH-50plus-BVH-TREE-1");

        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(0)
                .setRaysAmount(9), "BVH-50plus-BVH-TREE-0");

        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setMultithreading(4)
                .setRaysAmount(9), "BVH-50plus-BVH-TREE-4");
    }

    /**
     * Helper method to render an image using the configured camera builder and scene.
     * It also logs the total rendering time for benchmarking purposes.
     *
     * @param scene         the scene to render
     * @param cameraBuilder the fully configured camera builder
     * @param fileName      the output image file name
     */
    private void createImage(Scene scene, Camera.Builder cameraBuilder, String fileName) {
        long startTime = System.currentTimeMillis();

        Camera cameraOn = cameraBuilder
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

        cameraOn.renderImage();
        cameraOn.writeToImage(fileName);

        long endTime = System.currentTimeMillis();
        System.out.println("Render time for " + fileName + ": " + (endTime - startTime) / 1000.0 + " seconds.\n");
    }

    /**
     * Generates a dense grid of spheres around a given origin point and adds them
     * directly into the provided geometries container.
     *
     * @param container the root geometries container to add the spheres into
     * @param origin    the general center point of the cluster
     * @param nx        number of spheres along the X axis
     * @param ny        number of spheres along the Y axis
     * @param nz        number of spheres along the Z axis
     * @param spacing   distance between the centers of adjacent spheres
     * @param radius    base radius for each sphere
     * @param mat       the material to apply to the spheres
     * @param emission  the emission color of the spheres
     */
    @SuppressWarnings({"SameParameterValue"})
    private void buildSphereCluster(Geometries container, Point origin, int nx, int ny, int nz,
                                    double spacing, double radius,
                                    Material mat, Color emission) {

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

                    // Slight variation in radius to break symmetry
                    double r = radius * (0.85 + 0.3 * ((ix + iy + iz) % 3) / 3.0);

                    container.add(new Sphere(c, r)
                            .setEmission(emission)
                            .setMaterial(mat));
                }
            }
        }
    }
}
