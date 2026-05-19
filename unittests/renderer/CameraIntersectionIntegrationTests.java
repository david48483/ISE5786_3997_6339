package renderer;

import geometries.api.Intersectable;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration tests for {@link Camera#constructRay(int, int)} and
 * {@link Intersectable#findIntersections(Ray)} using Sphere, Plane, and Triangle geometries.
 *
 * @author David &amp; Yehuda
 */
public class CameraIntersectionIntegrationTests {

    /**
     * Resolution of the camera for the integration tests.
     */
    private static final int RESOLUTION = 3;

    /** Default constructor for the test class. */
    public CameraIntersectionIntegrationTests() {
    }

    /** Camera used in all integration tests. */
    private final Camera cam1 = new Camera.Builder()
            .setLocation(new Point(0, 0, 0))
            .setDirection(new Point(0, 0, -1))
            .setVpDistance(1)
            .setVpSize(3, 3)
            .setResolution(RESOLUTION, RESOLUTION)
            .build();

    /**
     * Counts the intersections between all rays constructed by the camera and a given geometry.
     *
     * @param camera        the camera under test
     * @param geometry      the geometry under test
     * @param expectedCount the expected total number of intersection points
     * @param testName      the assertion message
     */
    private void assertIntersectionsCount(Camera camera, Intersectable geometry, int expectedCount, String testName) {
        int count = 0;
        for (int i = 0; i < RESOLUTION; i++) {
            for (int j = 0; j < RESOLUTION; j++) {
                Ray ray = camera.constructRay(j, i);
                List<Point> intersections = geometry.findIntersections(ray);
                if (intersections != null) {
                    count += intersections.size();
                }
            }
        }
        assertEquals(expectedCount, count, testName);
    }

    /**
     * Tests camera rays against a sphere.
     */
    @Test
    void testCameraRaySphereIntegration() {

        Point point = new Point(0, 0, -3);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Sphere is in front of the camera and intersects only the center rays (2 points)
        Sphere sphere = new Sphere(point, 1);
        assertIntersectionsCount(cam1, sphere, 2, "Camera-Ray-Sphere Integration Test with 2 intersections");

        // EP02: Sphere is in front of the camera and intersects all rays (18 points)
        sphere = new Sphere(point, 2.5);
        assertIntersectionsCount(cam1, sphere, 18, "Camera-Ray-Sphere Integration Test with 18 intersections");

        // EP03: Sphere is in front of the camera and intersects some rays (10 points)
        sphere = new Sphere(point, 2.2);
        assertIntersectionsCount(cam1, sphere, 10, "Camera-Ray-Sphere Integration Test with 10 intersections");

        // EP04: Sphere is behind the camera (0 points)
        sphere = new Sphere(new Point(0, 0, 4), 1);
        assertIntersectionsCount(cam1, sphere, 0, "Camera-Ray-Sphere Integration Test with sphere behind the camera");

        // EP05: Camera is inside the sphere (9 points)
        sphere = new Sphere(point, 4);
        assertIntersectionsCount(cam1, sphere, 9, "Camera-Ray-Sphere Integration Test  with camera inside the sphere");

    }

    /**
     * Tests camera rays against a plane.
     */
    @Test
    void testCameraRayPlaneIntegration() {

        // ============ Equivalence Partitions Tests ==============
        // EP01: Plane is in front of the camera and intersects all rays (9 points)
        Plane plane = new Plane(new Point(0, 0, -5), new Vector(0, 0, 1));
        assertIntersectionsCount(cam1, plane, 9, "Camera-Ray-Plane Integration Test with 9 intersections");

        // EP02: Plane is in front of the camera, intersects all rays, and is not orthogonal to the camera (9 points)
        plane = new Plane(new Point(0, 0, -5), new Vector(0, 1, 2));
        assertIntersectionsCount(cam1, plane, 9, "Camera-Ray-Plane Integration Test with 9 intersections with non-orthogonal plane");

        // =============== Boundary Values Tests ==================

        // BVA01: Plane is in front of the camera and intersects some rays (6 points)
        plane = new Plane(new Point(0, 0, -5), new Vector(0, 1, 1));
        assertIntersectionsCount(cam1, plane, 6, "Camera-Ray-Plane Integration Test with 6 intersections");

    }

    /**
     * Tests camera rays against a triangle.
     */
    @Test
    void testCameraRayTriangleIntegration() {

        //  ============ Equivalence Partitions Tests ==============
        // EP01: Triangle is in front of the camera and intersects with 1 ray (1 point)
        Triangle triangle = new Triangle(new Point(0, 1, -2), new Point(-1, -1, -2), new Point(1, -1, -2));
        assertIntersectionsCount(cam1, triangle, 1, "Camera-Ray-Triangle Integration Test with 1 intersections");

        // EP02: Triangle is in front of the camera and intersects with some rays (2 points)
        triangle = new Triangle(new Point(0, 20, -2), new Point(-1, -1, -2), new Point(1, -1, -2));
        assertIntersectionsCount(cam1, triangle, 2, "Camera-Ray-Triangle Integration Test with 2 intersections");

    }

}
