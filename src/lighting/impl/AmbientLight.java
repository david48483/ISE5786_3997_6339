package lighting.impl;

import lighting.api.Light;
import primitives.Color;

/**
 * Represents ambient light in a 3D scene.
 * Ambient light is constant and directionless, affecting all objects equally.
 *
 * @author David &amp; Yehuda
 */
public final class AmbientLight extends Light {

    /**
     * Constant instance representing no ambient light.
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Creates ambient light with the specified intensity color.
     *
     * @param color intensity color of the ambient light
     */
    public AmbientLight(Color color) {
        super(color);
    }

}
