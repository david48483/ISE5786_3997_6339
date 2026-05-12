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

        public Builder setLocation(Point location)){
            return null;
        }

        public Builder setDirection(Vector to, Vector up){
            return null;
        }

        public Builder setDirection(Point target, Vector up){
            return null;
        }

        public Builder setDirection(Point target){
            return null;
        }

        public  Builder setVpDistance(double distance){
            return null;
        }

        public Builder setVpSize(double width, double height){
            return null;
        }

        public Builder setResolution (int nX, int nY){
            return null;
        }

        private calcVectors(){}

        private calcVpCenter(){}

        private checkAndSetResolution(){}

        private checkAndSetOrientation(){}

        private checkAndSetViewPlane(){}

        private checkResolution(){}

        private checkLocationAndDirection(){}

        private checkViewPlane(){}

        public Camera build(){
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();
            try {
                return (Camera)_camera.clone();
            } catch (CloneNotSupportedException _) {
                return null;
            }
        }


    }

}
