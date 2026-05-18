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
        assertIntersectionsCount(cam1, sphere, 2, "Camera-Ray-Sphere Integration Test with 2 intersection");

        //EP02 - Sphere is in front of the camera and intersects with all rays (18 points)
        sphere = new Sphere(point, 2.5);
        assertIntersectionsCount(cam1, sphere, 18, "Camera-Ray-Sphere Integration Test with 18 intersection");

        //  EP03 - Sphere is in front of the camera and intersects with some rays (10 points)
        sphere = new Sphere(point, 2.2);
        assertIntersectionsCount(cam1, sphere, 10, "Camera-Ray-Sphere Integration Test with 10 intersection");

        //  EP04 - Sphere is behind the camera (0 points)
        sphere = new Sphere(new Point(0, 0, 4), 1);
        assertIntersectionsCount(cam1, sphere, 0, "Camera-Ray-Sphere Integration Test with sphere behind the camera");

        //  EP05 - Camera is inside the sphere (9 points)
        sphere = new Sphere(point, 3);
        assertIntersectionsCount(cam1, sphere, 9, "Camera-Ray-Sphere Integration Test  with camera inside the sphere");

    }

    @Test
    void testCameraRayPlaneIntegration() {

    }

    @Test
    void testCameraRayTriangleIntegration() {

    }

}
