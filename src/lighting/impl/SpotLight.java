package lighting.impl;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static primitives.Util.alignZero;

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
     * The narrow beam factor for the spotlight. Higher values create a narrower beam.
     */
    private int _narrowBeam = 1;

    /**
     * Sets the narrow beam factor for the spotlight.
     *
     * @param narrowBeam the narrow beam factor (higher values create a narrower beam)
     * @return this spotlight instance for method chaining
     */
    public SpotLight setNarrowBeam(int narrowBeam) {
        _narrowBeam = narrowBeam;
        return this;
    }

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
        return (SpotLight) super.setKc(kC);
    }

    @Override
    public SpotLight setKl(double kL) {
        return (SpotLight) super.setKl(kL);
    }

    @Override
    public SpotLight setKq(double kQ) {
        return (SpotLight) super.setKq(kQ);
    }

    @Override
    public Color getIntensity(Point p) {
        Vector l;
        try {
            l = getL(p);
        } catch (Exception _) {
            return _intensity;
        }
        double projection = alignZero(_direction.dotProduct(l));

        if (projection <= 0) {
            return Color.BLACK;
        }

        double factor = Math.pow(projection, _narrowBeam);

        return super.getIntensity(p).scale(factor);
    }

}
