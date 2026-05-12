package renderer;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.MissingResourceException;

import static primitives.Util.isZero;

/**
 * Represents a pinhole camera used to construct rays through a view plane.
 *
 * @author David & Yehuda
 */
public class Camera implements Cloneable {

    /**
     * Default horizontal resolution used before an explicit value is set.
     */
    private static final int DEFAULT_RESOLUTION = 1;

    /**
     * Camera location in 3D space.
     */
    private Point _location;

    /**
     * Camera up direction.
     */
    private Vector _vUp;

    /**
     * Camera forward direction.
     */
    private Vector _vTo;

    /**
     * Camera right direction.
     */
    private Vector _vRight;

    /**
     * Distance from camera location to the view plane.
     */
    private Double _distance;

    /**
     * View plane width.
     */
    private Double _width;

    /**
     * View plane height.
     */
    private Double _height;

    /**
     * Horizontal resolution (number of columns).
     */
    private int _nx;

    /**
     * Vertical resolution (number of rows).
     */
    private int _ny;

    /**
     * Cached view plane center.
     */
    private Point _vpCenter;

    /**
     * Cached pixel width.
     */
    private Double _pixelWidth;

    /**
     * Cached pixel height.
     */
    private Double _pixelHeight;

    /**
     * Creates a camera with default resolution values.
     */
    private Camera() {
        _nx = DEFAULT_RESOLUTION;
        _ny = DEFAULT_RESOLUTION;
    }

    /**
     * Creates a new camera builder.
     *
     * @return a builder instance for camera configuration
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray from the camera location through the requested pixel.
     *
     * @param xIndex pixel column index
     * @param yIndex pixel row index
     * @return ray through the requested pixel
     */
    public Ray constructRay(int xIndex, int yIndex) {
        double xShift = (xIndex - (_nx - 1) / 2.0) * _pixelWidth;
        double yShift = -(yIndex - (_ny - 1) / 2.0) * _pixelHeight;

        Point pixelCenter = _vpCenter;

        if (!isZero(xShift)) {
            pixelCenter = pixelCenter.add(_vRight.scale(xShift));
        }
        if (!isZero(yShift)) {
            pixelCenter = pixelCenter.add(_vUp.scale(yShift));
        }

        return new Ray(_location, pixelCenter.subtract(_location));
    }

    /**
     * Builder for {@link Camera} instances.
     */
    public static class Builder {

        /**
         * Camera instance being configured.
         */
        private final Camera _camera;

        /**
         * Target point used by point-based direction setters.
         */
        private Point _target;

        /**
         * Up vector supplied with point-based direction setters.
         */
        private Vector _pendingUp;

        /**
         * Indicates whether direction should be computed from target point.
         */
        private boolean _useTargetDirection;

        /**
         * Creates a new builder with an empty camera.
         */
        public Builder() {
            _camera = new Camera();
        }

        /**
         * Sets the camera location.
         *
         * @param location camera location
         * @return this builder
         */
        public Builder setLocation(Point location) {
            _camera._location = location;
            return this;
        }

        /**
         * Sets explicit forward and up direction vectors.
         *
         * @param to forward direction
         * @param up up direction
         * @return this builder
         */
        public Builder setDirection(Vector to, Vector up) {
            _camera._vTo = to;
            _camera._vUp = up;
            _useTargetDirection = false;
            _target = null;
            _pendingUp = null;
            return this;
        }

        /**
         * Sets camera orientation by target point and explicit up vector.
         *
         * @param target target point to look at
         * @param up up direction
         * @return this builder
         */
        public Builder setDirection(Point target, Vector up) {
            _target = target;
            _pendingUp = up;
            _useTargetDirection = true;
            return this;
        }

        /**
         * Sets camera orientation by target point using the global Y axis as up direction.
         *
         * @param target target point to look at
         * @return this builder
         */
        public Builder setDirection(Point target) {
            _target = target;
            _pendingUp = Vector.AXIS_Y;
            _useTargetDirection = true;
            return this;
        }

        /**
         * Sets view plane distance from the camera.
         *
         * @param distance view plane distance
         * @return this builder
         */
        public Builder setVpDistance(double distance) {
            _camera._distance = distance;
            return this;
        }

        /**
         * Sets view plane size.
         *
         * @param width view plane width
         * @param height view plane height
         * @return this builder
         */
        public Builder setVpSize(double width, double height) {
            _camera._width = width;
            _camera._height = height;
            return this;
        }

        /**
         * Sets image resolution.
         *
         * @param nX number of columns
         * @param nY number of rows
         * @return this builder
         */
        public Builder setResolution(int nX, int nY) {
            _camera._nx = nX;
            _camera._ny = nY;
            return this;
        }

        /**
         * Computes and normalizes camera orientation vectors.
         */
        private void calcVectors() {
            _camera._vTo = _camera._vTo.normalize();
            _camera._vUp = _camera._vUp.normalize();
            _camera._vRight = _camera._vTo.crossProduct(_camera._vUp).normalize();
            _camera._vUp = _camera._vRight.crossProduct(_camera._vTo).normalize();
        }

        /**
         * Computes view plane center from location, forward vector, and distance.
         */
        private void calcVpCenter() {
            _camera._vpCenter = _camera._location.add(_camera._vTo.scale(_camera._distance));
        }

        /**
         * Validates and computes resolution-derived values.
         */
        private void checkAndSetResolution() {
            if (_camera._nx <= 0 || _camera._ny <= 0) {
                throw new IllegalArgumentException("Resolution must be positive");
            }
            _camera._pixelWidth = _camera._width / _camera._nx;
            _camera._pixelHeight = _camera._height / _camera._ny;
        }

        /**
         * Validates and computes orientation vectors.
         */
        private void checkAndSetOrientation() {
            if (_useTargetDirection) {
                if (_target == null) {
                    throw new MissingResourceException("Missing target point", "Camera", "target");
                }
                if (_pendingUp == null) {
                    throw new MissingResourceException("Missing up vector", "Camera", "vUp");
                }
                _camera._vTo = _target.subtract(_camera._location);
                _camera._vUp = _pendingUp;
            }

            if (_camera._vTo == null) {
                throw new MissingResourceException("Missing forward vector", "Camera", "vTo");
            }
            if (_camera._vUp == null) {
                throw new MissingResourceException("Missing up vector", "Camera", "vUp");
            }
            if (!isZero(_camera._vTo.dotProduct(_camera._vUp))) {
                throw new IllegalArgumentException("Forward and up vectors must be orthogonal");
            }

            calcVectors();
        }

        /**
         * Validates and computes view plane-derived values.
         */
        private void checkAndSetViewPlane() {
            if (_camera._width == null) {
                throw new MissingResourceException("Missing view plane width", "Camera", "width");
            }
            if (_camera._height == null) {
                throw new MissingResourceException("Missing view plane height", "Camera", "height");
            }
            if (_camera._distance == null) {
                throw new MissingResourceException("Missing view plane distance", "Camera", "distance");
            }
            if (_camera._width <= 0 || _camera._height <= 0 || _camera._distance <= 0) {
                throw new IllegalArgumentException("View plane size and distance must be positive");
            }

            calcVpCenter();
            checkAndSetResolution();
        }

        /**
         * Validates camera resolution.
         */
        private void checkResolution() {
            if (_camera._nx <= 0 || _camera._ny <= 0) {
                throw new IllegalArgumentException("Resolution must be positive");
            }
        }

        /**
         * Validates camera location and orientation.
         */
        private void checkLocationAndDirection() {
            if (_camera._location == null) {
                throw new MissingResourceException("Missing camera location", "Camera", "location");
            }
            checkAndSetOrientation();
        }

        /**
         * Validates view plane configuration.
         */
        private void checkViewPlane() {
            checkAndSetViewPlane();
        }

        /**
         * Builds and returns an immutable snapshot of the configured camera.
         *
         * @return built camera instance
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();
            try {
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException("Camera clone failed", e);
            }
        }
    }

}
