package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.api.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import java.util.List;

import static primitives.Util.alignZero;

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
    private Color calcColor(Intersection intersection, Vector v) {
        return !preprocessIntersection(intersection, v)? Color.BLACK :
                _scene.ambientLight.getIntensity()
                .scale(intersection.geometry.getMaterial().kA)
                .add(calcLocalEffects(intersection));

    }

    @Override
    Color traceRay(Ray ray) {
        List<Intersection> intersections = _scene.geometries.calcIntersections(ray);
        //List<Point> points = _scene.geometries.findIntersections(ray);
        return intersections == null ?
                _scene.background :
                //  calcColor(ray.findClosestPoint(points));
                calcColor(ray.findClosestIntersection(intersections), ray.direction());
    }

    private Color calcLocalEffects(Intersection intersection) {
        Color color = intersection.geometry.getEmission();

        for (LightSource lightSource : _scene.lights) {
            if (setLightSource(intersection, lightSource)) {
                color = color.add(
                        lightSource.getIntensity(intersection.point)
                                .scale(calcDiffuse(intersection, lightSource)
                                .add(calcSpecular(intersection)))
                );
            }
        }

        return color;

    }

    private Double3 calcDiffuse(Intersection intersection, LightSource light) {
        return intersection.material.kD.scale(Math.abs(intersection.lNormal));
    }

    private Double3 calcSpecular(Intersection intersection) {
        Vector r = intersection.l.add(intersection.normal.scale(-2 * intersection.lNormal));
        double minusVR = alignZero(-intersection.v.dotProduct(r));

        return minusVR <= 0
                ? Double3.ZERO
                : intersection.material.kS.scale(Math.pow(minusVR, intersection.material.nShininess));

    }

}
