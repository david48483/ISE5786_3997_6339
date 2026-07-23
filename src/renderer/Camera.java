package renderer;

import geometries.api.Intersectable;
import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import sampling.api.Sampler;
import sampling.impl.TargetShapeType;
import scene.Scene;

import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.stream.IntStream;

import static primitives.Util.alignZero;
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
     * Half of the X resolution value used for pixel-center calculations.
     */
    private double _halfNx;
    /**
     * Half of the Y resolution value used for pixel-center calculations.
     */
    private double _halfNy;

    /**
     * Image writer used to store rendered pixel colors.
     */
    private ImageWriter _imageWriter;

    /**
     * Ray tracer used to compute color from cast rays.
     */
    private RayTracerBase _rayTracer;

    /**
     * The number of threads to use for rendering. If set to 0, rendering will be single-threaded.
     */

    private int _threadsCount = 0;

    /**
     * Interval (in percent, 0.0–1.0) for progress debug printing during rendering.
     * 0 disables printing.
     */
    private double _printInterval = 0.0;

    /**
     * Helper that coordinates pixel assignment across threads and handles progress reporting.
     */
    private PixelManager _pixelManager = new PixelManager(_nY, _nX, _printInterval);

    /**
     * Default constructor for Camera. Initializes the camera with default values.
     * The camera's position, orientation, and view plane parameters must be set using the Builder before use.
     */
    private Camera() {
    }

    /**
     * Renders the current scene by casting one ray through each pixel.
     *
     * @return this camera instance
     */
    public Camera renderImage() {
        _pixelManager = new PixelManager(_nY, _nX, _printInterval);
        return switch (_threadsCount) {
            case 0 -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }

    /**
     * Renders the image sequentially on a single thread.
     * Intended for simplicity and deterministic debugging.
     *
     * @return this camera instance
     */
    public Camera renderImageNoThreads() {
        System.out.println("Rendering image sequentially (single-threaded)...");
        for (int j = 0; j < _nY; j++) {
            for (int i = 0; i < _nX; i++) {
                castRay(i, j);
            }
        }
        return this;
    }

    /**
     * Renders the image using a fixed pool of raw Java threads, each pulling
     * the next available pixel from {@link #_pixelManager} until all pixels are processed.
     *
     * @return this camera instance
     */
    private Camera renderImageRawThreads() {
        System.out.println("Rendering image using " + _threadsCount + " raw threads...");
        var threads = new LinkedList<Thread>();
        var count = _threadsCount;
        while (count-- > 0)
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                while ((pixel = _pixelManager.nextPixel()) != null) castRay(pixel.col(), pixel.row());
            }));
        for (var thread : threads) thread.start();
        try {
            for (var thread : threads) thread.join();
        } catch (InterruptedException _) {
        }
        return this;
    }

    /**
     * Renders the image using Java parallel streams (per-row and per-column).
     *
     * @return this camera instance
     */
    public Camera renderImageStream() {
        System.out.println("Rendering image using parallel streams...");
        IntStream.range(0, _nY).parallel()
                .forEach(yIndex -> IntStream.range(0, _nX).parallel().forEach(xIndex -> castRay(xIndex, yIndex)));
        return this;
    }

    /**
     * Draws a grid on top of the rendered image.
     *
     * @param interval line spacing in pixels
     * @param color    grid color
     * @return this camera instance
     */
    public Camera printGrid(int interval, Color color) {
        for (int j = 0; j < _nY; j++) {
            for (int i = 0; i < _nX; i++) {
                if (i % interval == 0 || j % interval == 0) {
                    _imageWriter.writePixel(i, j, color);
                }
            }
        }
        return this;
    }

    /**
     * Writes the current image buffer to a file.
     *
     * @param imageName output image file name without extension
     */
    public void writeToImage(String imageName) {
        _imageWriter.writeToImage(imageName);
    }

    /**
     * Casts a single ray through one pixel and writes the traced color.
     *
     * @param xIndex pixel column index
     * @param yIndex pixel row index
     */
    private void castRay(int xIndex, int yIndex) {
        Ray ray = constructRay(xIndex, yIndex);
        Color color = _rayTracer.traceRay(ray);
        _imageWriter.writePixel(xIndex, yIndex, color);
        _pixelManager.pixelDone();
    }
    //******************************************************************************************

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
     * @return a ray from the camera through the specified pixel
     */
    public Ray constructRay(int xIndex, int yIndex) {
        Point pIJ = _vpCenter;

        double xJ = (xIndex - _halfNx) * _pixelWidth;
        double yI = -(yIndex - _halfNy) * _pixelHeight;

        if (!isZero(xJ))
            pIJ = pIJ.add(_vRight.scale(xJ));
        if (!isZero(yI))
            pIJ = pIJ.add(_vUp.scale(yI));

        return new Ray(_p0, pIJ.subtract(_p0));
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
         * The beam generator used for creating sampling ray beams for glossy and diffusive effects.
         */
        private Sampler _sampler;

        /**
         * The shape of the target area used by the sampler (e.g., circle or square).
         */
        private TargetShapeType _samplerShapeType = TargetShapeType.SQUARE;

        /**
         * Flag indicating whether to use advanced rendering effects like Glossy Surfaces and Diffusive Glass.
         */
        private boolean _useAdvancedEffects = false;

        /**
         * The number of rays to generate for simulating glossy surfaces and diffusive glass.
         */
        private int _raysAmount = 1;

        /**
         * The target distance for the rays generated for simulating glossy surfaces and diffusive glass.
         */
        private double _targetDistance = 100d;

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
         * Sets the direction of the camera using a target point and an up vector.
         * The direction vector will be calculated from the camera's location to the target point.
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
         * Sets the direction of the camera using a target point.
         * The up vector will be set to the default (0, 1, 0).
         *
         * @param target the point in space that the camera is looking at
         * @return the Builder instance for method chaining
         */
        public Builder setDirection(Point target) {
            return setDirection(target, Vector.AXIS_Y);
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

        /**
         * Sets the ray tracer implementation for the camera.
         *
         * @param scene the scene used by the ray tracer
         * @param type  the requested ray tracer type
         * @return the Builder instance for method chaining
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                this._camera._rayTracer = new SimpleRayTracer(scene);
            } else {
                throw new IllegalArgumentException(type + " ray tracer is not supported");
            }
            return this;
        }

        /**
         * Set whether to use advanced rendering effects like Glossy Surfaces and Diffusive Glass.
         *
         * @param useAdvancedEffects true to enable advanced effects, false to disable
         * @return this Builder instance for method chaining
         */
        public Builder setUseAdvancedEffects(boolean useAdvancedEffects) {
            this._useAdvancedEffects = useAdvancedEffects;
            return this;
        }

        /**
         * Set the amount of rays for the beam (Grid of amount X amount).
         *
         * @param amount the amount of rays for the beam
         * @return this Builder instance for method chaining
         */
        public Builder setRaysAmount(int amount) {
            this._raysAmount = amount;
            return this;
        }

        /**
         * Sets the multithreading mode.
         * <ul>
         *   <li>threads = 0: single-threaded</li>
         *   <li>threads = -1: parallel streams</li>
         *   <li>threads = -2: use number of available processors</li>
         *   <li>threads > 0: fixed number of raw threads</li>
         * </ul>
         *
         * @param threads threading mode / number of threads
         * @return the Builder instance for method chaining
         * @throws IllegalArgumentException if threads < -2
         */
        public Builder setMultithreading(int threads) {
            if (threads < -2) throw new IllegalArgumentException("Multithreading must be -2 or higher");
            if (threads == -2) threads = Runtime.getRuntime().availableProcessors();
            this._camera._threadsCount = threads;
            return this;
        }

        /**
         * Enables periodic progress printing during rendering.
         *
         * @param printInterval interval in the range [0.0, 1.0]; 0 disables printing
         * @return the Builder instance for method chaining
         */
        public Builder setDebugPrint(double printInterval) {
            this._camera._printInterval = printInterval;
            return this;
        }

        /**
         * Sets the sampling strategy (Sampler) to be used by the beam generator.
         *
         * @param sampler the sampling strategy to set (e.g., GridSampler, JitteredSampler, RandomSampler)
         * @return this Builder instance for method chaining
         */
        public Builder setSampler(Sampler sampler) {
            this._sampler = sampler;
            return this;
        }

        /**
         * Sets the shape of the target area for the sampler.
         *
         * @param shape the desired target shape type
         * @return this Builder instance for method chaining
         */
        public Builder setSamplerShape(TargetShapeType shape) {
            this._samplerShapeType = shape;
            return this;
        }

        /**
         * Set the target distance for the rays generated for simulating glossy surfaces and diffusive glass.
         *
         * @param targetDistance the target distance for the rays
         * @return this Builder instance for method chaining
         */
        public Builder setTargetDistance(double targetDistance) {
            this._targetDistance = targetDistance;
            return this;
        }

        public Builder setBvhEnabled(boolean isEnabled) {
            Intersectable.setBvhEnabled(isEnabled);
            return this; // החזרת הבילדר כדי לאפשר שרשור (Chaining)
        }

        /**
         * Computes the orthonormal camera basis vectors.
         */
        private void calcVectors() {
            try {
                _camera._vRight = _camera._vTo.crossProduct(_up).normalize();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Up vector cannot be parallel to the direction vector");
            }
            _camera._vUp = _camera._vRight.crossProduct(_camera._vTo).normalize();
        }

        /**
         * Computes the view plane center and derived pixel values.
         */
        private void calcVpCenter() {
            _camera._pixelWidth = _camera._width / _camera._nX;
            _camera._pixelHeight = _camera._height / _camera._nY;
            _camera._halfNx = (_camera._nX - 1) / 2.0;
            _camera._halfNy = (_camera._nY - 1) / 2.0;
            _camera._vpCenter = _camera._p0.add(_camera._vTo.scale(_camera._distance));
        }

        /**
         * Validates that the location and direction information was provided.
         */
        private void checkLocationAndDirection() {
            if (_camera._p0 == null)
                throw new MissingResourceException(
                        "Camera location is missing",
                        Camera.class.getName(),
                        "_p0");

            if (_up == null)
                throw new MissingResourceException(
                        "Up vector is missing",
                        Camera.class.getName(),
                        "_up");

            if (_target == null && _direction == null)
                throw new MissingResourceException(
                        "Camera direction is missing",
                        Camera.class.getName(),
                        "_direction");

            if (_direction == null) {
                _camera._vTo = _target.subtract(_camera._p0).normalize();
            } else {
                _camera._vTo = _direction.normalize();
            }
            calcVectors();
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
            _camera._imageWriter = new ImageWriter(_camera._nX, _camera._nY);
        }

        /**
         * Validates the view plane parameters of the camera and calculates derived values.
         * Ensures that the width, height, and distance to the view plane are positive.
         *
         * @throws IllegalArgumentException if any of the view plane parameters are not valid
         */
        private void checkViewPlane() {
            if (alignZero(_camera._width) <= 0 || alignZero(_camera._height) <= 0 || alignZero(_camera._distance) <= 0) {
                throw new IllegalArgumentException("View plane size must be positive");
            }
            calcVpCenter();
        }

        /**
         * Validates that a ray tracer is configured; if missing, sets a default tracer.
         */
        private void checkRayTracer() {
            if (_camera._rayTracer == null) {
                setRayTracer(new Scene("test"), RayTracerType.SIMPLE);
            }
            if (_camera._rayTracer instanceof SimpleRayTracer simpleRayTracer) {
                simpleRayTracer.setUseAdvancedEffects(_useAdvancedEffects);
                if (_sampler != null) {
                    _sampler.setTargetShape(_samplerShapeType);
                    simpleRayTracer.setSampler(_sampler);
                }

                simpleRayTracer.setRaysAmount(_raysAmount);
                simpleRayTracer.setTargetDistance(_targetDistance);
            }

        }

        /**
         * Validates the camera parameters and builds the final Camera object.
         *
         * @return a fully constructed Camera object
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();
            checkRayTracer();
            try {
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException _) {
                return null;
            }
        }

    }

}
