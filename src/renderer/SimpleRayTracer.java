package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.api.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import sampling.BeamGenerator;
import scene.Scene;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * Basic ray tracer implementation that shades visible intersections
 * using ambient and local lighting effects.
 *
 * @author David &amp; Yehuda
 */
class SimpleRayTracer extends RayTracerBase {

    /**
     * Maximum recursion depth for color calculation to prevent infinite recursion.
     */
    private static final int MAX_CALC_COLOR_LEVEL = 10;

    /**
     * Minimum color contribution threshold for terminating recursion in color calculation.
     */
    private static final double MIN_CALC_COLOR_K = 0.001;

    /**
     * Initial color contribution factor for recursion in color calculation.
     */
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * Creates a simple ray tracer for the given scene.
     *
     * @param scene the scene to trace
     */
    SimpleRayTracer(Scene scene) {
        super(scene);

    }

    /**
     * Prepares light-dependent shading data for an intersection and a light source.
     *
     * @param intersection the intersection point data
     * @return true if the intersection is unshaded (not in shadow) for the light source, false otherwise
     */
    private boolean unshaded(Intersection intersection) {
        Vector pointToLight = intersection.l.scale(-1);

        Ray shadowRay = new Ray(intersection.point, pointToLight, intersection.normal);

        double lightDistance = intersection.light.getDistance(intersection.point);

        var intersections = _scene.geometries.calcIntersections(shadowRay, lightDistance);
        if (intersections == null) return true;
        for (Intersection i : intersections) {
            if (i.material.kT.isLowerThan(MIN_CALC_COLOR_K)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Calculates the aggregated transparency attenuation factor along the shadow ray.
     *
     * @param intersection the intersection point data
     * @return the cumulative transparency factor (Double3)
     */
    private Double3 transparency(Intersection intersection) {
        Vector pointToLight = intersection.l.scale(-1);
        Ray shadowRay = new Ray(intersection.point, pointToLight, intersection.normal);
        double lightDistance = intersection.light.getDistance(intersection.point);

        var intersections = _scene.geometries.calcIntersections(shadowRay, lightDistance);

        Double3 ktr = Double3.ONE;

        if (intersections == null) return ktr;

        for (Intersection i : intersections) {

            ktr = ktr.product(i.material.kT);

            if (ktr.isLowerThan(MIN_CALC_COLOR_K)) {
                return Double3.ZERO;
            }
        }

        return ktr;
    }

    /**
     * Constructs a reflection ray based on the intersection point and incoming ray direction.
     *
     * @param intersection the intersection point data
     * @return the reflection ray
     */
    private Ray constructReflectionRay(Intersection intersection) {

        // formula for calculate reflection ray : r = v - 2 * (v * n) * n
        Vector r = intersection.v.subtract(intersection.normal.scale(2 * intersection.vNormal));

        return new Ray(intersection.point, r, intersection.normal);
    }

    /**
     * Constructs a transparency (refraction) ray based on the intersection point and incoming ray direction.
     *
     * @param intersection the intersection point data
     * @return the transparency ray
     */
    private Ray constructTransparencyRay(Intersection intersection) {

        return new Ray(intersection.point, intersection.v, intersection.normal);
    }

    /**
     * Calculates the global lighting effect (reflection or transparency) for a given ray.
     *
     * @param ray   the ray to trace for global effects
     * @param level the current recursion level
     * @param k     the accumulated color contribution factor
     * @param kx    the material's reflection or transparency coefficient
     * @return the resulting color contribution from global effects
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
        Double3 kkx = k.product(kx);
        if (kkx.isLowerThan(MIN_CALC_COLOR_K)) return Color.BLACK;

        Intersection intersection = findClosestIntersection(ray);

        if (intersection == null) return _scene.background.scale(kx);

        if (!preprocessIntersection(intersection, ray.direction())) return Color.BLACK;

        return calcColor(intersection, level - 1, kkx).scale(kx);
    }

    /**
    * Calculates the combined global effects (reflection and transparency) at an intersection point.
    *
    * @param intersection a prepared intersection containing the point, normal, and material properties
    * @param level the current recursion level for color calculation
    * @param k the accumulated color contribution factor from previous calculations
    * @return the resulting color contribution from both reflection and transparency effects
    */
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
        return calcGlobalEffect(
                constructTransparencyRay(intersection),
                level,
                k,
                intersection.material.kT,

                intersection.material.kB,
                intersection.normal
        )
                .add(calcGlobalEffect(
                        constructReflectionRay(intersection),
                        level,
                        k,
                        intersection.material.kR,
                        intersection.material.kG,
                        intersection.normal
                ));
    }

    /**
     * Calculates the global lighting effect (reflection or transparency) for a given ray.
     * Supports glossy surfaces and diffusive glass with explosion prevention.
     *
     * @param ray the ray to trace for global effects
     * @param level the current recursion level
     * @param k the accumulated color contribution factor
     * @param kx the material's reflection or transparency coefficient
     * @param radius the radius for generating rays for glossy or diffusive effects
     * @param normal the surface normal at the intersection point
     * @return the resulting color contribution from global effects
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx, double radius, Vector normal) {
        Double3 kkx = k.product(kx);
        if (kkx.isLowerThan(MIN_CALC_COLOR_K)) return Color.BLACK;

        int actualRaysAmount = (level == MAX_CALC_COLOR_LEVEL) ? _raysAmount : 1;

        if (!_useAdvancedEffects || radius == 0 || actualRaysAmount <= 1) {
            Intersection intersection = findClosestIntersection(ray);
            if (intersection == null) return _scene.background.scale(kx);

            return preprocessIntersection(intersection, ray.direction()) ?
                    calcColor(intersection, level - 1, kkx).scale(kx) : Color.BLACK;
        }

        BeamGenerator beamGenerator = new BeamGenerator();

        Beam beam = beamGenerator.generateBeam(ray, radius, _targetDistance, actualRaysAmount);

        Color colorSum = Color.BLACK;
        List<Ray> rays = beam.getRays();
        int validRaysCount = 0;

        double idealDirectionSign = alignZero(ray.direction().dotProduct(normal));

        for (Ray beamRay : rays) {
            double rayDirectionSign = alignZero(beamRay.direction().dotProduct(normal));

            if (idealDirectionSign * rayDirectionSign > 0) {
                validRaysCount++;
                Intersection intersection = findClosestIntersection(beamRay);

                if (intersection == null) {
                    colorSum = colorSum.add(_scene.background.scale(kx));
                } else if (preprocessIntersection(intersection, beamRay.direction())) {
                    colorSum = colorSum.add(calcColor(intersection, level - 1, kkx).scale(kx));
                }
            }
        }

        if (validRaysCount == 0) return Color.BLACK;

        return colorSum.reduce(validRaysCount);
    }

    @Override
    Color traceRay(Ray ray) {
        Intersection closestIntersection = findClosestIntersection(ray);
        // List<Intersection> intersections = _scene.geometries.calcIntersections(ray);
        //List<Point> points = _scene.geometries.findIntersections(ray);
        return closestIntersection == null ?
                _scene.background :
                //  calcColor(ray.findClosestPoint(points));
                //  calcColor(ray.findClosestIntersection(intersections), ray.direction());
                calcColor(closestIntersection, ray.direction());
    }

    /**
     * Finds the closest intersection point along a given ray.
     *
     * @param ray the ray to trace for intersections
     * @return the closest intersection point, or null if no intersections exist
     */
    private Intersection findClosestIntersection(Ray ray) {
        List<Intersection> intersections = _scene.geometries.calcIntersections(ray);
        return intersections == null ? null : ray.findClosestIntersection(intersections);
    }

    /**
     * Calculates the color at an intersection point.
     *
     * @param intersection the closest intersection point
     * @param v            normalized view direction
     * @return the resulting color at the intersection
     */
    private Color calcColor(Intersection intersection, Vector v) {
        return !preprocessIntersection(intersection, v) ? Color.BLACK :
                _scene.ambientLight.getIntensity().scale(intersection.geometry.getMaterial().kA)
                        //.add(calcLocalEffects(intersection));
                        .add(calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K));
    }

    /**
     * Calculates the color at an intersection point with recursion for reflection and refraction.
     *
     * @param intersection the closest intersection point
     * @param level        the current recursion level
     * @param k            the accumulated color contribution factor
     * @return the resulting color at the intersection
     */
    private Color calcColor(Intersection intersection, int level, Double3 k) {
        Color color = calcLocalEffects(intersection, k);

        return 1 == level ? color : color.add(calcGlobalEffects(intersection, level, k));
    }

    /**
     * Calculates local lighting effects (emission, diffuse, and specular)
     * at the given intersection.
     *
     * @param intersection the prepared intersection data
     * @param k            the accumulated color contribution factor
     * @return resulting local color contribution
     */
    private Color calcLocalEffects(Intersection intersection, Double3 k) {
        Color color = intersection.geometry.getEmission();

        for (LightSource lightSource : _scene.lights) {
            if (setLightSource(intersection, lightSource)) {
                Double3 ktr = transparency(intersection);

                Double3 lightFactor = ktr.product(
                        calcDiffuse(intersection, lightSource).add(calcSpecular(intersection))
                );

                if (lightFactor.product(k).isGreaterThan(MIN_CALC_COLOR_K)) {
                    color = color.add(lightSource.getIntensity(intersection.point).scale(lightFactor));
                }
            }
        }

        return color;
    }

    /**
     * Calculates the diffuse reflection coefficient for the current light.
     *
     * @param intersection the prepared intersection data
     * @return diffuse coefficient per channel
     */
    private Double3 calcDiffuse(Intersection intersection) {
        return intersection.material.kD.scale(Math.abs(intersection.lNormal));
    }

    /**
     * Calculates the specular reflection coefficient.
     *
     * @param intersection the prepared intersection data
     * @return specular coefficient per channel
     */
    private Double3 calcSpecular(Intersection intersection) {
        Vector r = intersection.l.add(intersection.normal.scale(-2 * intersection.lNormal));

        double minusVR = alignZero(-intersection.v.dotProduct(r));
        return minusVR <= 0
                ? Double3.ZERO
                : intersection.material.kS.scale(Math.pow(minusVR, intersection.material.nShininess));
    }

}
