package renderer;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.MissingResourceException;

import static primitives.Util.isZero;

public class Camera implements Cloneable {

    private Point _p0;

    private Vector _vUp;

    private Vector _vTo;

    private Vector _vRight;

    private double _distance;

    private double _width;

    private double _height;

    private int _nX = 1;

    private int _nY = 1;

    private Point _vpCenter;

    private double _pixelWidth;

    private double _pixelHeight;

    private Camera() {
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public Ray constructRay(int xIndex, int yIndex) {
        Point pIJ = _vpCenter;

        double xJ = (xIndex - (_nX - 1) / 2.0) * _pixelWidth;
        double yI = -(yIndex - (_nY - 1) / 2.0) * _pixelHeight;

        if (!isZero(xJ))
            pIJ = pIJ.add(_vRight.scale(xJ));
        if (!isZero(yI))
            pIJ = pIJ.add(_vUp.scale(yI));

        return new Ray(_p0, pIJ.subtract(_p0).normalize());

    }

    ;

    public static class Builder {

        private final Camera _camera;

        private Vector _direction;
        private Point _target;
        private Vector _up;

        public Builder() {
            _camera = new Camera();
        }

        public Builder setLocation(Point location) {

            _camera._p0 = location;
            return this;
        }

        public Builder setDirection(Vector to, Vector up) {

            _direction = to;
            _up = up;
            _target = null;

            return this;
        }

        public Builder setDirection(Point target, Vector up) {

            _direction = null;
            _up = up;
            _target = target;

            return this;
        }

        public Builder setDirection(Point target) {

            _direction = null;
            _up = Vector.AXIS_Y;
            _target = target;

            return this;
        }

        public Builder setVpDistance(double distance) {

            this._camera._distance = distance;
            return this;
        }

        public Builder setVpSize(double width, double height) {

            this._camera._width = width;
            this._camera._height = height;
            return this;
        }

        public Builder setResolution(int nX, int nY) {

            this._camera._nX = nX;
            this._camera._nY = nY;
            return this;
        }

        private void calcVectors() {
        }

        private void calcVpCenter() {
        }

        private void checkLocationAndDirection() {
            if (_camera._p0 == null || _up == null || (_target == null && _direction == null))
                throw new MissingResourceException("Camera location and direction must be set", Camera.class.getName(), "");

            if (_direction == null) {
                _camera._vTo = _target.subtract(_camera._p0).normalize();
            } else {
                _camera._vTo = _direction.normalize();
            }
            try {
                _camera._vRight = _camera._vTo.crossProduct(_up).normalize();

            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Up vector cannot be parallel to the direction vector");
            }

            _camera._vUp = _camera._vRight.crossProduct(_camera._vTo).normalize();

        }

        private void checkResolution() {

            if (_camera._nX <= 0 || _camera._nY <= 0) {
                throw new IllegalArgumentException("Resolution must be positive");

            }
        }

        private void checkViewPlane() {

            if (_camera._width <= 0 || _camera._height <= 0 || _camera._distance <= 0) {
                throw new IllegalArgumentException("View plane size must be positive");
            }
            _camera._pixelWidth = _camera._width / _camera._nX;
            _camera._pixelHeight = _camera._height / _camera._nY;

            _camera._vpCenter = _camera._p0.add(_camera._vTo.scale(_camera._distance));
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
