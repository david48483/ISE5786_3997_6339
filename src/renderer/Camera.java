package renderer;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.MissingResourceException;

import static primitives.Util.isZero;

/**
 * Represents a camera in 3D space, defined by its position, orientation, and view plane parameters.
 *
 * @author David &amp; Yehuda
 */

public class Camera implements Cloneable {

    /**
     * The position of the camera in 3D space.
     */
    private Point _p0;
    /**
     * The up vector of the camera, defining the vertical orientation.
     */
    private Vector _vUp;
    /**
     * The direction vector of the camera, pointing from the camera towards the scene.
     */
    private Vector _vTo;
    /**
     * The right vector of the camera, perpendicular to both the direction and up vectors.
     */
    private Vector _vRight;
    /**
     * The distance from the camera to the view plane.
     */
    private double _distance;
    /**
     * The width of the view plane.
     */
    private double _width;
    /**
     * The height of the view plane.
     */
    private double _height;
    /**
     * The number of pixels in the X direction (horizontal resolution).
     */
    private int _nX = 1;
    /**
     * The number of pixels in the Y direction (vertical resolution).
     */
    private int _nY = 1;
    /**
     * The center point of the view plane.
     */
    private Point _vpCenter;
    /**
     * The width of a single pixel on the view plane.
     */
    private double _pixelWidth;
    /**
     * The height of a single pixel on the view plane.
     */
    private double _pixelHeight;
    /**
     * The half of the number of pixels in the X direction, used for calculating pixel positions.
     */
    private double _halfNx;
    /**
     * The half of the number of pixels in the Y direction, used for calculating pixel positions.
     */
    private double _halfNy;

    /**
     * Default constructor for Camera. Initializes the camera with default values.
     * The camera's position, orientation, and view plane parameters must be set using the Builder before use.
     */
    private Camera() {
    }

    /**
     * Returns a new Builder instance for constructing a Camera object.
     *
     * @return a new Builder instance
     */

    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray from the camera through the center of the specified pixel on the view plane.
     *
     * @param xIndex the x-coordinate index of the pixel
     * @param yIndex the y-coordinate index of the pixel
     * @return a Ray object representing the ray from the camera through the specified pixel
     */
    public Ray constructRay(int xIndex, int yIndex) {
        Point pIJ = _vpCenter;

        double xJ = (xIndex - _halfNx) * _pixelWidth;
        double yI = -(yIndex - _halfNy) * _pixelHeight;

        if (!isZero(xJ))
            pIJ = pIJ.add(_vRight.scale(xJ));
        if (!isZero(yI))
            pIJ = pIJ.add(_vUp.scale(yI));

        return new Ray(_p0, pIJ.subtract(_p0).normalize());

    }

    /**
     * Builder class for constructing Camera instances with a fluent interface.
     */
    public static class Builder {
        /**
         * The Camera instance being built.
         */
        private final Camera _camera;
        /**
         * The direction vector for the camera, if set directly.
         */
        private Vector _direction;
        /**
         * The target point for the camera, if set using a target point.
         */
        private Point _target;
        /**
         * The up vector for the camera, defining its vertical orientation.
         */
        private Vector _up;

        /**
         * Default constructor for the Builder. Initializes a new Camera instance to be configured.
         */
        public Builder() {
            _camera = new Camera();
        }

        /**
         * Sets the location of the camera in 3D space.
         *
         * @param location the position of the camera
         * @return the Builder instance for method chaining
         */
        public Builder setLocation(Point location) {

            _camera._p0 = location;
            return this;
        }

        /**
         * Sets the direction of the camera using a direction vector and an up vector.
         *
         * @param to the direction vector pointing from the camera towards the scene
         * @param up the up vector defining the camera's vertical orientation
         * @return the Builder instance for method chaining
         */
        public Builder setDirection(Vector to, Vector up) {

            _direction = to;
            _up = up;
            _target = null;

            return this;
        }

        /**
         * Sets the direction of the camera using a target point and an up vector. The direction vector will be calculated from the camera's location to the target point.
         *
         * @param target the point in space that the camera is looking at
         * @param up     the up vector defining the camera's vertical orientation
         * @return the Builder instance for method chaining
         */

        public Builder setDirection(Point target, Vector up) {

            _direction = null;
            _up = up;
            _target = target;

            return this;
        }

        /**
         * Sets the direction of the camera using a target point. The up vector will be set to the default (0, 1, 0).
         *
         * @param target the point in space that the camera is looking at
         * @return the Builder instance for method chaining
         */
        public Builder setDirection(Point target) {

            _direction = null;
            _up = Vector.AXIS_Y;
            _target = target;

            return this;
        }

        /**
         * Sets the distance from the camera to the view plane.
         *
         * @param distance the distance from the camera to the view plane
         * @return the Builder instance for method chaining
         */
        public Builder setVpDistance(double distance) {

            this._camera._distance = distance;
            return this;
        }

        /**
         * Sets the size of the view plane (width and height).
         *
         * @param width  the width of the view plane
         * @param height the height of the view plane
         * @return the Builder instance for method chaining
         */

        public Builder setVpSize(double width, double height) {

            this._camera._width = width;
            this._camera._height = height;
            return this;
        }

        /**
         * Sets the resolution of the view plane (number of pixels in X and Y directions).
         *
         * @param nX the number of pixels in the X direction
         * @param nY the number of pixels in the Y direction
         * @return the Builder instance for method chaining
         */
        public Builder setResolution(int nX, int nY) {

            this._camera._nX = nX;
            this._camera._nY = nY;
            return this;
        }

        //====================================================================================================================================================================

        /**
         * private void calcVectors() {
         * }
         * <p>
         * private void calcVpCenter() {
         * }
         */
        //====================================================================================================================================================================
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

        /**
         * Validates the resolution parameters of the camera.
         * Ensures that the number of pixels in both X and Y directions is positive.
         *
         * @throws IllegalArgumentException if the resolution parameters are not valid
         */
        private void checkResolution() {

            if (_camera._nX <= 0 || _camera._nY <= 0) {
                throw new IllegalArgumentException("Resolution must be positive");

            }
        }

        /**
         * Validates the view plane parameters of the camera and calculates derived values.
         * Ensures that the width, height, and distance to the view plane are positive.
         * Calculates the pixel size, half pixel counts, and the center point of the view plane based on the camera's position and orientation.
         *
         * @throws IllegalArgumentException if any of the view plane parameters are not valid
         */
        private void checkViewPlane() {

            if (_camera._width <= 0 || _camera._height <= 0 || _camera._distance <= 0) {
                throw new IllegalArgumentException("View plane size must be positive");
            }
            _camera._pixelWidth = _camera._width / _camera._nX;
            _camera._pixelHeight = _camera._height / _camera._nY;

            _camera._halfNx = (_camera._nX - 1) / 2.0;
            _camera._halfNy = (_camera._nY - 1) / 2.0;

            _camera._vpCenter = _camera._p0.add(_camera._vTo.scale(_camera._distance));
        }

        /**
         * Validates the camera parameters and builds the final Camera object.
         *
         * @return a fully constructed Camera object...
         */
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
