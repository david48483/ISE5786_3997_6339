package lighting.impl;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class SpotLight extends PointLight {

    private final Vector _direction;

    SpotLight(Color color, Point point, Vector vector) {
        super(color, point);
        _direction = vector.normalize();
    }
}
