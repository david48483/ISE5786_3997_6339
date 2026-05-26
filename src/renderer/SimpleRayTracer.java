package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

import java.util.List;

class SimpleRayTracer extends RayTracerBase {

    SimpleRayTracer(Scene scene) {
        super(scene);

    }

    private Color calcColor(Point intersection) {
        return _scene.ambientLight.getIntensity();

    }

    @Override
    Color traceRay(Ray ray) {
        List<Point> points = _scene.geometries.findIntersections(ray);
        if (points == null)
            return _scene.background;
        Point closestPoint = ray.findClosestPoint(points);
        return calcColor(closestPoint);
    }

}
