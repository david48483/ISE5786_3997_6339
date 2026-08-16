package sampling.api;

import primitives.Ray;

import java.util.List;

/**
 * A class representing a beam of rays in a 3D scene.
 * A beam is a collection of rays that can be used for various rendering techniques,
 * such as anti-aliasing or soft shadows.
 *
 * @author David &amp; Yehuda
 */
public class Beam {

    /**
     * The list of rays that make up the beam.
     * Each ray in the list represents a different path through the scene,
     * allowing for more accurate rendering of complex lighting effects.
     */
    private final List<Ray> _rays;

    /**
     * Creates a new Beam instance with the specified list of rays.
     *
     * @param rays A list of Ray objects that make up the beam
     */
    public Beam(List<Ray> rays) {
        _rays = rays;
    }

    /**
     * Returns the list of rays that make up the beam.
     *
     * @return A list of Ray objects that make up the beam
     */
    public List<Ray> getRays() {
        return _rays;
    }
}