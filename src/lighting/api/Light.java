package lighting.api;

import primitives.Color;

public abstract class Light {

    protected Light(Color color) {
        _intensity = color;
    }

    /**
     * Ambient light intensity as a color value.
     */
    protected final Color _intensity;

    /**
     * Returns the ambient light intensity.
     *
     * @return ambient light intensity color
     */
    public Color getIntensity() {
        return _intensity;
    }
}
