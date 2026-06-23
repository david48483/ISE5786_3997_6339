package scene;

import geometries.impl.Geometries;
import lighting.api.LightSource;
import lighting.impl.AmbientLight;
import primitives.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a 3D scene configuration used by the renderer.
 * Contains scene-level properties such as background color,
 * ambient light, and scene geometries.
 *
 * @author David &amp; Yehuda
 */
public class Scene {

    /**
     * Scene name.
     */
    public String name;

    /**
     * Scene background color.
     */
    public Color background = Color.BLACK;

    /**
     * Ambient light applied to the entire scene.
     */
    public AmbientLight ambientLight = AmbientLight.NONE;

    /**
     * Collection of geometries in the scene.
     */
    public Geometries geometries = new Geometries();

    public List<LightSource> lights = new ArrayList<>();

    /**
     * Creates a scene with the given name.
     *
     * @param name scene name
     */
    public Scene(String name) {
        this.name = name;
    }

    /**
     * Sets the scene background color.
     *
     * @param background background color
     * @return this scene instance
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the ambient light of the scene.
     *
     * @param ambientLight ambient light value
     * @return this scene instance
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Sets the scene geometries collection.
     *
     * @param geometries geometries collection
     * @return this scene instance
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }

}
