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
 * Integration tests for the Camera, Ray, and geometric shapes (Sphere, Plane, Triangle).
 * Integration tests for {@link Camera#constructRay(int, int)} and {@link Intersectable#findIntersections(Ray)}.
 * <p>
 * These tests verify that the rays constructed by the camera correctly intersect with various geometric shapes...
 *
 * @author David &amp; Yehuda
 */

public class CameraIntersectionIntegrationTests {

    /**
     * Resolution of the camera for the integration tests.
     */
    private static final int RESOLUTION = 3;

    /**
     * Default constructor for the test class.
     */
    public CameraIntersectionIntegrationTests() {
    }

    /**
     * Camera object for use in integration tests.
     */
    private final Camera cam1 = new Camera.Builder()
            .setLocation(new Point(0, 0, 0))
            .setDirection(new Point(0, 0, -1))
            .setVpDistance(1)
            .setVpSize(3, 3)
            .setResolution(RESOLUTION, RESOLUTION)
            .build();

    /**
     * Helper method to assert the number of intersections between rays constructed by the camera and a given geometry.
     *
     * @param camera        the camera from which rays are constructed
     * @param geometry      the geometric shape to test for intersections
     * @param expectedCount the expected number of intersections
     * @param testName      the name of the test for reporting purposes
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
     * Test method for the integration of Camera, Ray, and Sphere.
     * This test verifies that the rays constructed by the camera correctly intersect with a sphere in various scenarios.
     * The test covers different positions and sizes of the sphere relative to the camera, including cases where the sphere is in front of, behind, or encompassing the camera.
     * The expected number of intersections is calculated based on the geometric configuration of the sphere and the rays, ensuring that the camera's ray construction and the sphere's intersection logic are working together correctly.
     */
    @Test
    void testCameraRaySphereIntegration() {

        Point point = new Point(0, 0, -3);

        //sphere: X^2+Y^2+ (Z+3)^2=R^2
        //ray: P0 + t*V = (0,0,0) + t*(x,y,-1) = (tx, ty, -t)
        //intersection: (tx)^2 + (ty)^2 + (-t+3)^2 = R^2
        //t^2(x^2+y^2+1) - 6t + 9 - R^2 = 0
        //r=x^2+y^2
        //t^2(r+1) - 6t + 9 - R^2 = 0
        //2 intersection iff b^2-4ac>0=======    R^2>(9r^2/r^2+1)
        //(calculate for x,y: (0,0)==0<R^2<(4.5),(0,1)==(4.5)<R^2<(6),(1,1)==R^2>(6):

        // ============ Equivalence Partitions Tests ==============

        // EP01 - Sphere is in front of the camera and intersects with the center rays only (2 points)
        Sphere sphere = new Sphere(point, 1);
        assertIntersectionsCount(cam1, sphere, 2, "Camera-Ray-Sphere Integration Test with 2 intersections");

        //EP02 - Sphere is in front of the camera and intersects with all rays (18 points)
        sphere = new Sphere(point, 2.5);
        assertIntersectionsCount(cam1, sphere, 18, "Camera-Ray-Sphere Integration Test with 18 intersections");

        //  EP03 - Sphere is in front of the camera and intersects with some rays (10 points)
        sphere = new Sphere(point, 2.2);
        assertIntersectionsCount(cam1, sphere, 10, "Camera-Ray-Sphere Integration Test with 10 intersections");

        //  EP04 - Sphere is behind the camera (0 points)
        sphere = new Sphere(new Point(0, 0, 4), 1);
        assertIntersectionsCount(cam1, sphere, 0, "Camera-Ray-Sphere Integration Test with sphere behind the camera");

        //  EP05 - Camera is inside the sphere (9 points)
        sphere = new Sphere(point, 4);
        assertIntersectionsCount(cam1, sphere, 9, "Camera-Ray-Sphere Integration Test  with camera inside the sphere");

    }

    /**
     * Test method for the integration of Camera, Ray, and Plane.
     * This test verifies that the rays constructed by the camera correctly intersect with a plane in various scenarios.
     * The test covers different positions and orientations of the plane relative to the camera, including cases where the plane is in front of, behind, or at an angle to the camera.
     * The expected number of intersections is calculated based on the geometric configuration of the plane and the rays, ensuring that the camera's ray construction and the plane's intersection logic are working together correctly.
     */
    @Test
    void testCameraRayPlaneIntegration() {

        // ============ Equivalence Partitions Tests ==============
        // EP01 - Plane is in front of the camera and intersects with all rays (9 points)
        Plane plane = new Plane(new Point(0, 0, -5), new Vector(0, 0, 1));
        assertIntersectionsCount(cam1, plane, 9, "Camera-Ray-Plane Integration Test with 9 intersections");

        //  EP02 - Plane is in front of the camera and intersects with all rays but not orthogonal to the camera (9 points)
        plane = new Plane(new Point(0, 0, -5), new Vector(0, 1, 2));
        assertIntersectionsCount(cam1, plane, 9, "Camera-Ray-Plane Integration Test with 9 intersections with non-orthogonal plane");

        // =============== Boundary Values Tests ==================

        // BV01 - Plane is in front of the camera and intersects with some rays (6 points)
        plane = new Plane(new Point(0, 0, -5), new Vector(0, 1, 1));
        assertIntersectionsCount(cam1, plane, 6, "Camera-Ray-Plane Integration Test with 6 intersections");

    }

    /**
     * Test method for the integration of Camera, Ray, and Triangle.
     * This test verifies that the rays constructed by the camera correctly intersect with a triangle in various scenarios.
     * The test covers different positions and orientations of the triangle relative to the camera, including cases where the triangle is in front of, behind, or at an angle to the camera.
     * The expected number of intersections is calculated based on the geometric configuration of the triangle and the rays, ensuring that the camera's ray construction and the triangle's intersection logic are working together correctly.
     */
    @Test
    void testCameraRayTriangleIntegration() {

        //  ============ Equivalence Partitions Tests ==============
        // EP01 - Triangle is in front of the camera and intersects with 1 rays (1 point)
        Triangle triangle = new Triangle(new Point(0, 1, -2), new Point(-1, -1, -2), new Point(1, -1, -2));
        assertIntersectionsCount(cam1, triangle, 1, "Camera-Ray-Triangle Integration Test with 1 intersections");

        //  EP02 - Triangle is in front of the camera and intersects with some rays (2 points)
        triangle = new Triangle(new Point(0, 20, -2), new Point(-1, -1, -2), new Point(1, -1, -2));
        assertIntersectionsCount(cam1, triangle, 2, "Camera-Ray-Triangle Integration Test with 2 intersections");

    }

}
