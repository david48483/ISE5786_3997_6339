package renderer;

import geometries.api.Intersectable.Intersection;
import primitives.Color;
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
    private Color calcColor(Intersection intersection) {
        return _scene.ambientLight.getIntensity()
                .scale(intersection.geometry.getMaterial().kA)
                .add(intersection.geometry.getEmission());

    }

    @Override
    Color traceRay(Ray ray) {
        List<Intersection> intersections = _scene.geometries.calcIntersections(ray);
        //List<Point> points = _scene.geometries.findIntersections(ray);
        return intersections == null ?
                _scene.background :
                //  calcColor(ray.findClosestPoint(points));
                calcColor(ray.findClosestIntersection(intersections));
    }

}
