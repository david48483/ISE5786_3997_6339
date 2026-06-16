package lighting.impl;

import lighting.api.Light;
import lighting.api.LightSource;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class PointLight extends Light implements LightSource {

    protected Point _position;

    private double _kC = 0.0;
    private double _kL = 0.0;
    private double _kQ = 0.0;

    public PointLight(Color color, Point position) {
        super(color);
        _position = position;
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
