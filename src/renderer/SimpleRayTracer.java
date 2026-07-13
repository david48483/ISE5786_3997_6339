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
 * using ambient and local lighting effects.
 *
 * @author David &amp; Yehuda
 */
class SimpleRayTracer extends RayTracerBase {

    /**
     * Small delta value used to offset rays to avoid self-intersection.
     */
    private static final double DELTA = 0.1;

    /**
     * Creates a simple ray tracer for the given scene.
     *
     * @param scene the scene to trace
     */
    SimpleRayTracer(Scene scene) {
        super(scene);

    }

    /**
     * Prepares light-dependent shading data for an intersection and a light source.
     *
     * @param intersection
     * @return
     */
    private boolean unshaded(Intersection intersection) {
        Vector pointToLight = intersection.l.scale(-1);
        Vector delta = intersection.normal.scale(intersection.lNormal < 0 ? DELTA : -DELTA);
        Ray shadowRay = new Ray(intersection.point.add(delta), pointToLight);
        return _scene.geometries.findIntersections(shadowRay) == null;
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

    /**
     * Calculates the color at an intersection point.
     *
     * @param intersection the closest intersection point
     * @param v            normalized view direction
     * @return the resulting color at the intersection
     */
    private Color calcColor(Intersection intersection, Vector v) {
        return !preprocessIntersection(intersection, v) ? Color.BLACK :
                _scene.ambientLight.getIntensity().scale(intersection.geometry.getMaterial().kA)
                        .add(calcLocalEffects(intersection));
    }

    /**
     * Calculates local lighting effects (emission, diffuse, and specular)
     * at the given intersection.
     *
     * @param intersection the prepared intersection data
     * @return resulting local color contribution
     */
    private Color calcLocalEffects(Intersection intersection) {
        Color color = intersection.geometry.getEmission();

        for (LightSource lightSource : _scene.lights) {
            if (setLightSource(intersection, lightSource) && (unshaded(intersection))) {
                color = color.add(
                        lightSource.getIntensity(intersection.point)
                                .scale(calcDiffuse(intersection, lightSource)
                                        .add(calcSpecular(intersection)))
                );
            }
        }

        return color;
    }

    /**
     * Calculates the diffuse reflection coefficient for the current light.
     *
     * @param intersection the prepared intersection data
     * @param light        active light source
     * @return diffuse coefficient per channel
     */
    private Double3 calcDiffuse(Intersection intersection, LightSource light) {
        return intersection.material.kD.scale(Math.abs(intersection.lNormal));
    }

    /**
     * Calculates the specular reflection coefficient.
     *
     * @param intersection the prepared intersection data
     * @return specular coefficient per channel
     */
    private Double3 calcSpecular(Intersection intersection) {
        Vector r = intersection.l.add(intersection.normal.scale(-2 * intersection.lNormal));

        double minusVR = alignZero(-intersection.v.dotProduct(r));
        return minusVR <= 0
                ? Double3.ZERO
                : intersection.material.kS.scale(Math.pow(minusVR, intersection.material.nShininess));
    }

}
