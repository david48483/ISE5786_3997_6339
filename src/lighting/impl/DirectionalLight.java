package lighting.impl;

import lighting.api.Light;
import lighting.api.LightSource;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Directional light source with constant direction and constant intensity.
 *
 * @author David &amp; Yehuda
 */
public class DirectionalLight extends Light implements LightSource {

    /**
     * Normalized direction of the light rays.
     */
    private final Vector _direction;

    /**
     * Creates a directional light with the given color and direction.
     *
     * @param color  light intensity color
     * @param vector light direction
     */
    public DirectionalLight(Color color, Vector vector) {
        super(color);
        _direction = vector.normalize();
    }

    @Override
    public Vector getL(Point p) {
        return _direction;
    }

    @Override
    public Color getIntensity(Point p) {
        return _intensity;
    }
}
