package lighting;

import primitives.Color;

public final class AmbientLight {

    private final Color _intensity;
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    public AmbientLight(Color color) {
        _intensity = color;
    }

    public Color getIntensity() {
        return _intensity;
    }

}
