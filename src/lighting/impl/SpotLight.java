package lighting.impl;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class SpotLight extends PointLight {

    private final Vector _direction;

    public SpotLight(Color color, Point position, Vector vector) {
        super(color, position);
        _direction = vector.normalize();
    }
}
