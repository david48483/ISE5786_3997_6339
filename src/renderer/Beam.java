package renderer;

import primitives.Ray;

import java.util.List;

public class Beam {
    private final List<Ray> _rays;

    public Beam(List<Ray> rays) {
        _rays = rays;
    }

    public List<Ray> getRays() {
        return _rays;
    }
}