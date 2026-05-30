package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

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

}
