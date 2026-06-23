package renderer;

import geometries.api.Intersectable;
import lighting.api.LightSource;
import primitives.Color;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import static primitives.Util.alignZero;

/**
 * Base abstraction for ray tracers.
 * Implementations define how a ray is evaluated into a resulting color.
 *
 * @author David &amp; Yehuda
 */
abstract class RayTracerBase {

    /**
     * The scene used for ray tracing calculations.
     */
    protected Scene _scene;

    /**
     * Traces a single ray through the scene and returns its computed color.
     *
     * @param ray the ray to trace
     * @return the resulting color for the ray
     */
    abstract Color traceRay(Ray ray);

    /**
     * Creates a ray tracer for the given scene.
     *
     * @param scene the scene to trace rays in
     */
    RayTracerBase(Scene scene) {
        _scene = scene;
    }

    /**
     * Prepares view-dependent shading data at a specific intersection.
     *
     * @param intersection the intersection to prepare
     * @param v            normalized view direction
     * @return {@code true} if the view direction is not orthogonal to the normal
     */
    protected boolean preprocessIntersection(Intersectable.Intersection intersection, Vector v) {
        intersection.v = v;
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.vNormal = alignZero(intersection.v.dotProduct(intersection.normal));
        return intersection.vNormal != 0;
    }

    /**
     * Prepares light-dependent shading data for an intersection and a light source.
     *
     * @param intersection the intersection to prepare
     * @param light        active light source
     * @return {@code true} if the light and view are on the same side of the surface
     */
    protected boolean setLightSource(Intersectable.Intersection intersection, LightSource light) {
        intersection.light = light;
        intersection.l = light.getL(intersection.point);
        intersection.lNormal = alignZero(intersection.l.dotProduct(intersection.normal));
        return intersection.lNormal * intersection.vNormal > 0;
    }

}
