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

    @Override
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    public SpotLight setKq(double kQ) {
        super.setKq(kQ);
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        try {
            double projection = _direction.dotProduct(getL(p));
            return projection <= 0 ? Color.BLACK : super.getIntensity(p).scale(projection);
        } catch (Exception e) {
            return _intensity;
        }

    }
}
