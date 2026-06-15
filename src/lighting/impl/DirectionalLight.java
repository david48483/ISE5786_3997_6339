package lighting.impl;

import lighting.api.Light;
import lighting.api.LightSource;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class DirectionalLight extends Light implements LightSource {

    private final Vector _direction;

    public DirectionalLight(Color color, Vector vector) {
        super(color);
        _direction = vector.normalize();
    }

    @Override
    public Vector getL(Point p) {
        return null;
    }

    @Override
    public Color getIntensity(Point p) {
        return null;
    }
}
