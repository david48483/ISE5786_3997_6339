package renderer;

/**
 * Enumerates the available ray tracing strategies.
 * Used to select which ray tracer implementation the renderer should use.
 *
 * @author David &amp; Yehuda
 */
public enum RayTracerType {
    /**
     * A basic ray tracer.
     */
    SIMPLE,

    /**
     * A ray tracer that uses a regular grid acceleration structure.
     */
    GRID
}
