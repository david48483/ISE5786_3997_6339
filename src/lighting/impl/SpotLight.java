package lighting.impl;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Spot light source with directional focus and point-light attenuation.
 *
 * @author David &amp; Yehuda
 */
public class SpotLight extends PointLight {

    /**
     * Normalized spotlight direction.
     */
    private final Vector _direction;

    /**
     * Creates a spotlight with the given color, position, and direction.
     *
     * @param color    light intensity color
     * @param position light position
     * @param vector   spotlight direction
     */
    public SpotLight(Color color, Point position, Vector vector) {
        super(color, position);
        _direction = vector.normalize();
    }

    @Override
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    @Override
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
