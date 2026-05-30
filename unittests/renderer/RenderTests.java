package renderer;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import scene.Scene;

import static java.awt.Color.YELLOW;

/**
 * End-to-end rendering tests.
 * <p>
 * These tests exercise the full rendering flow:
 * scene setup -> camera setup -> ray tracing -> image output.
 * </p>
 *
 * @author David &amp; Yehuda
 */
@SuppressWarnings("java:S109")
class RenderTests {
    /**
     * Default constructor to satisfy documentation tools.
     */
    RenderTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Physical view-plane size (square: SIZE x SIZE).
     */
    static final double VP_SIZE = 500;
    /**
     * Distance from camera to view plane.
     */
    static final double VP_DISTANCE = 100;

    /**
     * Camera location.
     */
    static final Point LOCATION = Point.ZERO;
    /**
     * Camera look-at target point.
     */
    static final Point LOOK_AT = new Point(0, 0, -1);
    /**
     * Image resolution (square: N x N).
     */
    static final int RESOLUTION = 1000;

    /**
     * Creates a base camera builder used by rendering tests.
     *
     * @return camera builder configured with common test parameters
     */
    private static Camera.Builder baseCameraBuilder() {
        return Camera.getBuilder() //
                .setLocation(LOCATION).setDirection(LOOK_AT) //
                .setVpDistance(VP_DISTANCE).setVpSize(VP_SIZE, VP_SIZE) //
                .setResolution(RESOLUTION, RESOLUTION);
    }

    /**
     * Renders a basic two-color scene and overlays a grid.
     */
    @Test
    void testBasicRenderTwoColors() {
        Scene scene = new Scene("Two colors")                   //
                .setBackground(new Color(75, 127, 90))                       //
                .setAmbientLight(new AmbientLight(new Color(255, 191, 191)));

        final double Z = -100D;
        // Left, Middle, Right X Bottom, Middle, Top
        Point pLM = new Point(-100, 0, Z);
        Point pMT = new Point(0, 100, Z);
        Point pLT = new Point(-100, 100, Z);
        Point pMB = new Point(0, -100, Z);
        Point pLB = new Point(-100, -100, Z);
        Point pRM = new Point(100, 0, Z);
        Point pRB = new Point(100, -100, Z);
        Point o = new Point(0, 0, Z);
        double radius = 50D;

        scene.geometries //
                .add(// center
                        new Sphere(o, radius),
                        // up left
                        new Triangle(pLM, pMT, pLT),
                        // down left
                        new Triangle(pLM, pMB, pLB),
                        // down right
                        new Triangle(pRM, pMB, pRB));

        baseCameraBuilder() //
                .setRayTracer(scene, RayTracerType.SIMPLE) //
                .build() //
                .renderImage() //
                .printGrid(100, new Color(YELLOW)) //
                .writeToImage("Two colors render test");
    }

    /**
     * Renders a scene loaded from an XML file.
     * <p>
     * Parsing logic should be implemented in dedicated production code,
     * not inside unit tests.
     * </p>
     *
     * @param builder camera builder to use
     * @param xmlName XML scene file name
     * @return the rendered camera instance
     */
    Camera renderSceneXML(Camera.Builder builder, String xmlName) {
        Scene scene = new Scene("Using XML");
        // Parse from XML file into scene object instead of the new Scene above,
        // using the code added in dedicated parser packages.
        // NB: unit tests are not the right place to implement XML parsing.

        return builder //
                .setRayTracer(scene, RayTracerType.SIMPLE) //
                .build() //
                .renderImage(); //
    }

    /**
     * Renders a scene loaded from a JSON file.
     * <p>
     * Parsing logic should be implemented in dedicated production code,
     * not inside unit tests.
     * </p>
     *
     * @param builder camera builder to use
     * @param jsonName JSON scene file name
     * @return the rendered camera instance
     */
    static Camera renderSceneJSON(Camera.Builder builder, String jsonName) {
        Scene scene = new Scene("Using JSON");
        // Parse from JSON file into scene object instead of the new Scene above,
        // using the code added in dedicated parser packages.
        // NB: unit tests are not the right place to implement JSON parsing.

        return builder //
                .setRayTracer(scene, RayTracerType.SIMPLE) //
                .build() //
                .renderImage(); //
    }

    /**
     * Bonus test for XML-based scene rendering.
     */
    @Test
    void testBasicRenderXml() {
        renderSceneXML(baseCameraBuilder(), "basicRenderTestTwoColors") //
                .printGrid(100, new Color(YELLOW)) //
                .writeToImage("render test xml");
    }

    /**
     * Bonus test for JSON-based scene rendering.
     */
    @Test
    void testBasicRenderJson() {
        renderSceneJSON(baseCameraBuilder(), "basicRenderTestTwoColors") //
                .printGrid(100, new Color(YELLOW)) //
                .writeToImage("render test json");
    }
}
