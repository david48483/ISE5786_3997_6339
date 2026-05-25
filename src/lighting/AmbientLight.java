package lighting;

import primitives.Color;

/**
 * Represents ambient light in a 3D scene.
 * Ambient light is a constant, directionless light that illuminates all objects equally.
 * It is used to simulate indirect lighting and prevent completely dark areas in the scene.
 */

public final class AmbientLight {

    /**
     * The intensity of the ambient light, represented as a color.
     */
    private final Color _intensity;

    /**
     * A constant representing no ambient light (black color).
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructs an ambient light with the specified intensity.
     *
     * @param color the color representing the intensity of the ambient light
     */
    public AmbientLight(Color color) {
        _intensity = color;
    }

    /**
     * Returns the intensity of the ambient light.
     *
     * @return the color representing the intensity of the ambient light
     */
    public Color getIntensity() {
        return _intensity;
    }

}
