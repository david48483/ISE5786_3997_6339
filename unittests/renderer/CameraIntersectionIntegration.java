package renderer;

import geometries.api.Intersectable;
import geometries.impl.Sphere;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CameraIntersectionIntegration {

    Camera cam1 = new Camera.Builder()
            .setLocation(new Point(0, 0, 0))
            .setDirection(new Point(0, 0, -1))
            .setVpDistance(1)
            .setVpSize(3, 3)
            .setResolution(3, 3)
            .build();

    private void assertIntersectionsCount(Camera camera, Intersectable geometry, int expectedCount, String testName) {
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Ray ray = camera.constructRay(j, i);
                List<Point> intersections = geometry.findIntersections(ray);
                if (intersections != null) {
                    count += intersections.size();
                }
            }
        }
        assertEquals(expectedCount, count, testName);
    }

    @Test
    void testCameraRaySphereIntegration() {

        Sphere sphere = new Sphere(Point.ZERO, 1);
        int i, j, sum = 0;
        for (i = 0; i < 3; i++)
            for (j = 0; j < 3; j++) {
                Ray ray = cam1.constructRay(j, i);
                List<Point> intersections = sphere.findIntersections(ray);
                if (intersections != null)
                    sum = sum + intersections.size();
            }
        assertEquals(9, sum, "ERROR: Camera-Sphere integration test failed");
    }

    @Test
    void testCameraRayPlaneIntegration() {

    }

    @Test
    void testCameraRayTriangleIntegration() {

    }

}
