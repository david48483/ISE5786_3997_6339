package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

abstract class RayTracerBase {

    protected Scene _scene;

    abstract Color traceRay(Ray ray);

    RayTracerBase(Scene scene) {
        _scene = scene;
    }

}

