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
import sampling.impl.JitteredSampler;
import sampling.impl.TargetShapeType;
import scene.Scene;

/**
 * Unit tests for rendering scenes with glossy and diffusive materials, comparing the effects of enabling and disabling advanced rendering features.
 *
 * @author David &amp; Yehuda
 */
public class GlossyDiffusiveMultiTests {

    /**
     *
     * Creates a new glossy and diffusive test suite.
     */
    public GlossyDiffusiveMultiTests() {
    }

    /**
     * Test method for rendering a scene with glossy and diffusive materials, comparing the effects of enabling and disabling advanced rendering features.
     */
    @Test
    public void testGlossyAndDiffusiveSimulation() {
        Scene scene = new Scene("Glossy and Diffusive Simulation Scene");

        scene.lights.add(new SpotLight(new Color(700, 400, 400), new Point(60, 50, 100), new Vector(-1, -1, -3))
                .setKl(4E-4).setKq(2E-5));

        scene.lights.add(new PointLight(new Color(500, 250, 250), new Point(-60, -50, 100))
                .setKl(0.0005).setKq(0.0005));

        scene.lights.add(new DirectionalLight(new Color(200, 200, 200), new Vector(1, -1, -1)));

        scene.geometries.add(new Plane(new Point(0, -70, 0), Vector.AXIS_Y)
                .setMaterial(new Material().setKD(0.2).setKS(0.2).setShininess(30)
                        .setKR(1).setKG(7.0)));

        scene.geometries.add(new Plane(new Point(0, 0, -200), Vector.AXIS_Z)
                .setMaterial(new Material().setKD(0.5).setKS(0.1).setShininess(10)));

        scene.geometries.add(new Sphere(new Point(0, 0, -50), 30)
                .setEmission(new Color(20, 40, 80))
                .setMaterial(new Material().setKD(0.2).setKS(0.5).setShininess(70)
                        .setKT(0.8).setKB(100.0)));

        scene.geometries.add(new Sphere(new Point(70, 20, -70), 20)
                .setEmission(new Color(30, 0, 0))
                .setMaterial(new Material().setKD(0.2).setKS(0.8).setShininess(200)
                        .setKR(1)));

        scene.geometries.add(new Sphere(new Point(-70, 20, -70), 20)
                .setEmission(new Color(0, 40, 0))
                .setMaterial(new Material().setKD(0.7).setKS(0.2).setShininess(50)));

        scene.geometries.add(new Sphere(new Point(0, 60, -120), 10)
                .setEmission(new Color(80, 80, 0))
                .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(30)));

        Point p1 = new Point(-40, -69, -30);
        Point p2 = new Point(-20, -69, -30);
        Point p3 = new Point(-30, -69, -50);
        Point pTop = new Point(-30, -45, -40);

        Material pyramidMat = new Material().setKD(0.5).setKS(0.5).setShininess(60);
        scene.geometries.add(new Triangle(p1, p2, pTop).setMaterial(pyramidMat));
        scene.geometries.add(new Triangle(p2, p3, pTop).setMaterial(pyramidMat));
        scene.geometries.add(new Triangle(p3, p1, pTop).setMaterial(pyramidMat));
        scene.geometries.add(new Triangle(p1, p2, p3).setMaterial(pyramidMat));

        // Common camera base for all runs
        Camera.Builder cameraBuilder = Camera.getBuilder()
                .setLocation(new Point(0, 0, 500))
                .setDirection(new Vector(0, 0, -1), Vector.AXIS_Y)
                .setVpSize(200, 200)
                .setVpDistance(500)
                .setSamplerShape(TargetShapeType.CIRCLE)
                .setResolution(600, 600);

        // 2. With Jittered Sampler
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setSamplerShape(TargetShapeType.CIRCLE)
                .setDebugPrint(0.1)
                .setMultithreading(-2)
                .setRaysAmount(9), "multi-2");

        // 2. With Jittered Sampler
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setSamplerShape(TargetShapeType.CIRCLE)
                .setDebugPrint(0.1)
                .setMultithreading(-1)
                .setRaysAmount(9), "multi-1");

        // 2. With Jittered Sampler
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setSamplerShape(TargetShapeType.CIRCLE)
                .setDebugPrint(0.1)
                .setMultithreading(-0)
                .setRaysAmount(9), "multi-0");

        // 2. With Jittered Sampler
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setSamplerShape(TargetShapeType.CIRCLE)
                .setDebugPrint(0.1)
                .setMultithreading(1)
                .setRaysAmount(9), "multi_1");

        // 2. With Jittered Sampler
        createImage(scene, cameraBuilder
                .setUseAdvancedEffects(true)
                .setSampler(new JitteredSampler())
                .setSamplerShape(TargetShapeType.CIRCLE)
                .setDebugPrint(0.1)
                .setMultithreading(8)
                .setRaysAmount(9), "multi_4");
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
}