package renderer;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

public class Camera implements Cloneable {

    private final Point _p0;

    private final Vector _vUp;

    private final Vector _vTo;

    private final Vector _vRight;

    private Double _distance;

    private Double _width;

    private Double _height;

    private int _nx;

    private int _ny;

    private Point _vpCenter;

    private Double _pixelWidth;

    private Double _pixelHeight;

    private Camera() {
    }

    public static Builder getBuilder() {
        return null;
    }

    public Ray constructRay(int column, int raw) {
        return null;
    }

    ;

    public static class Builder {

        private final Camera _camera = null;

    }

}
