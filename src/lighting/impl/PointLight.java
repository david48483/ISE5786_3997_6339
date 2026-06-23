package lighting.impl;

import lighting.api.Light;
import lighting.api.LightSource;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class PointLight extends Light implements LightSource {

    protected Point _position;

    private double _kC = 1.0;
    private double _kL = 0.0;
    private double _kQ = 0.0;

    public PointLight setKc(double kC) {
        _kC = kC;
        return this;
    }

    public PointLight setKl(double kL) {
        _kL = kL;
        return this;
    }

    public PointLight setKq(double kQ) {
        _kQ = kQ;
        return this;
    }

    public PointLight(Color color, Point position) {
        super(color);
        _position = position;
    }

    @Override
    public Vector getL(Point p) {
        return p.subtract(_position).normalize();
    }

    @Override
    public Color getIntensity(Point p) {
        return super.getIntensity().scale(1.0 / (_kC + _kL * p.distance(_position) + _kQ * (p.distanceSquared(_position))));
    }

}
