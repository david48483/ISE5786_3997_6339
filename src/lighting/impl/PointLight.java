package lighting.impl;

import lighting.api.Light;
import lighting.api.LightSource;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Point light source with distance attenuation.
 *
 * @author David &amp; Yehuda
 */
public class PointLight extends Light implements LightSource {

    /**
     * Position of the light source.
     */
    private final Point _position;

    /**
     * Constant attenuation factor.
     */
    private double _kC = 1.0;
    /**
     * Linear attenuation factor.
     */
    private double _kL = 0.0;
    /**
     * Quadratic attenuation factor.
     */
    private double _kQ = 0.0;

    /**
     * Sets the constant attenuation factor.
     *
     * @param kC constant attenuation factor
     * @return this light instance
     */
    public PointLight setKc(double kC) {
        _kC = kC;
        return this;
    }

    /**
     * Sets the linear attenuation factor.
     *
     * @param kL linear attenuation factor
     * @return this light instance
     */
    public PointLight setKl(double kL) {
        _kL = kL;
        return this;
    }

    /**
     * Sets the quadratic attenuation factor.
     *
     * @param kQ quadratic attenuation factor
     * @return this light instance
     */
    public PointLight setKq(double kQ) {
        _kQ = kQ;
        return this;
    }

    /**
     * Creates a point light with the given color and position.
     *
     * @param color    light intensity color
     * @param position light position
     */
    public PointLight(Color color, Point position) {
        super(color);
        _position = position;
    }

    /**
     * Returns the direction vector from a point on the surface to the light source.
     *
     * @param p the point on the surface
     * @return normalized direction vector from the point to the light
     */
    @Override
    public Vector getL(Point p) {
        return p.subtract(_position).normalize();
    }

    @Override
    public Color getIntensity(Point p) {
        return _intensity.scale(1.0 / (_kC + _kL * p.distance(_position) + _kQ * (p.distanceSquared(_position))));
    }

    @Override
    public double getDistance(Point point) {
        return point.distance(_position);
    }

}
