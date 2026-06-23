package lighting.api;

import primitives.Color;

/**
 * Base type for all light implementations.
 * Stores the intrinsic light intensity color shared by concrete light types.
 *
 * @author David &amp; Yehuda
 */
public abstract class Light {

    /**
     * Intrinsic light intensity color.
     */
    protected final Color _intensity;

    /**
     * Creates a light with the given intrinsic intensity color.
     *
     * @param color intrinsic light intensity color
     */
    protected Light(Color color) {
        _intensity = color;
    }

    /**
     * Returns the intrinsic light intensity.
     *
     * @return intrinsic light intensity color
     */
    public Color getIntensity() {
        return _intensity;
    }
}
