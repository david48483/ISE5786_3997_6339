package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

import java.util.List;

/**
 * Basic ray tracer implementation that shades visible intersections
 * using ambient light only.
 *
 * @author David &amp; Yehuda
 */
class SimpleRayTracer extends RayTracerBase {

    /**
     * Creates a simple ray tracer for the given scene.
     *
     * @param scene the scene to trace
     */
    SimpleRayTracer(Scene scene) {
        super(scene);

    }

    /**
     * Calculates the color at an intersection point.
     *
     * @param intersection the closest intersection point
     * @return the resulting color at the intersection
     */
    private Color calcColor(Point intersection) {
        return _scene.ambientLight.getIntensity();

    }

    @Override
    Color traceRay(Ray ray) {
        List<Point> points = _scene.geometries.findIntersections(ray);
        return points == null ?
             _scene.background :
            calcColor(ray.findClosestPoint(points));
    }

}
