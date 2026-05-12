package renderer;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.MissingResourceException;

public class Camera implements Cloneable {

    private Point _p0;

    private Vector _vUp;

    private Vector _vTo;

    private Vector _vRight;

    private Double _distance;

    private Double _width;

    private Double _height;

    private int _nX = 1;

    private int _nY = 1;

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

        private final Camera _camera;

        private Point _target;

        private Vector _up;

        private Vector _direction;

        // private boolean _ttt;

        public Builder() {
            _camera = new Camera();
        }

        public Builder setLocation(Point location) {
            _camera._p0 = location;
            return this;
        }

        public Builder setDirection(Vector to, Vector up) {

            _camera._vTo = to;
            _camera._vUp = up;

            //v right
            return this;
        }

        public Builder setDirection(Point target, Vector up) {
            _target = target;
            _up = up;

            return this;
        }

        public Builder setDirection(Point target) {

            _target = target;
            _up = Vector.AXIS_Y;
            return this;
        }

        public Builder setVpDistance(double distance) {
            _camera._distance = distance;
            return this;
        }

        public Builder setVpSize(double width, double height) {

            _camera._width = width;
            _camera._height = height;
            return this;
        }

        public Builder setResolution(int nX, int nY) {

            _camera._nX = nX;
            _camera._nY = nY;
            return this;
        }

        private void calcVectors() {
        }

        private void calcVpCenter() {
        }

        private void checkAndSetResolution() {
        }

        private void checkAndSetOrientation() {
        }

        private void checkAndSetViewPlane() {
        }

        private void checkResolution() {
            if (_camera._nX <= 0 || _camera._nY <= 0) {
                throw new IllegalArgumentException("Resolution must be positive");
            }

        }

        private void checkLocationAndDirection() {

            if (_camera._p0 == null)
                throw new MissingResourceException("Camera location is not set", "Camera", "location");

            if (_camera._vTo == null)

                throw new MissingResourceException("Camera direction is not set", "Camera", "direction");
            if (_camera._vUp == null)
                MissingResourceException("Camera up vector is not set");

        }

        private void checkViewPlane() {
        }

        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();
            try {
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException _) {
                return null;
            }
        }

    }

}
