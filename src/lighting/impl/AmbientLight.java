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
     * Ambient light intensity as a color value.
     */
    // private final Color _intensity;

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
        // _intensity = color;
        super(color);
    }

    /**
     * Returns the ambient light intensity.
     *
     * @return ambient light intensity color
     */
    // public Color getIntensity() {
    //   return _intensity;
    //}

}
